package com.ptmharsha.urlshortener.service.impl;

import com.ptmharsha.urlshortener.config.AppProperties;
import com.ptmharsha.urlshortener.dto.request.ShortenUrlRequest;
import com.ptmharsha.urlshortener.dto.response.ShortenUrlResponse;
import com.ptmharsha.urlshortener.entity.UrlMapping;
import com.ptmharsha.urlshortener.exception.DuplicateAliasException;
import com.ptmharsha.urlshortener.exception.InvalidURLException;
import com.ptmharsha.urlshortener.exception.ResourceNotFoundException;
import com.ptmharsha.urlshortener.repository.UrlMappingRepository;
import com.ptmharsha.urlshortener.util.ShortCodeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock private UrlMappingRepository repository;
    @Mock private ShortCodeGenerator generator;
    @Mock private AppProperties appProperties;
    @InjectMocks private UrlServiceImpl service;

    @Test
    void createsAUrlWithAGeneratedCode() {
        ShortenUrlRequest request = request("https://example.com", null);
        when(appProperties.getBaseUrl()).thenReturn("http://localhost:8081");
        when(repository.findByOriginalUrl(request.getUrl())).thenReturn(Optional.empty());
        when(repository.existsByShortCode("Ab3dE9F")).thenReturn(false);
        when(generator.generate()).thenReturn("Ab3dE9F");
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ShortenUrlResponse response = service.shortenUrl(request);

        assertEquals("Ab3dE9F", response.getShortCode());
        assertEquals("http://localhost:8081/Ab3dE9F", response.getShortUrl());
    }

    @Test
    void retriesWhenAGeneratedCandidateAlreadyExists() {
        ShortenUrlRequest request = request("https://example.com", null);
        when(appProperties.getBaseUrl()).thenReturn("http://localhost:8081");
        when(repository.findByOriginalUrl(request.getUrl())).thenReturn(Optional.empty());
        when(generator.generate()).thenReturn("taken00", "fresh01");
        when(repository.existsByShortCode("taken00")).thenReturn(true);
        when(repository.existsByShortCode("fresh01")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("fresh01", service.shortenUrl(request).getShortCode());
    }

    @Test
    void returnsTheExistingMappingForTheSameUrlWithoutAnAlias() {
        UrlMapping existing = UrlMapping.builder().originalUrl("https://example.com").shortCode("existing").build();
        when(appProperties.getBaseUrl()).thenReturn("http://localhost:8081");
        when(repository.findByOriginalUrl("https://example.com")).thenReturn(Optional.of(existing));

        assertEquals("existing", service.shortenUrl(request("https://example.com", null)).getShortCode());
        verify(repository, never()).save(any());
    }

    @Test
    void acceptsAnAvailableCustomAlias() {
        ShortenUrlRequest request = request("https://example.com", "docs");
        when(appProperties.getBaseUrl()).thenReturn("http://localhost:8081");
        when(repository.existsByShortCode("docs")).thenReturn(false);
        when(repository.findByOriginalUrl(request.getUrl())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertEquals("docs", service.shortenUrl(request).getShortCode());
    }

    @Test
    void rejectsAnAliasThatIsAlreadyInUse() {
        when(repository.existsByShortCode("docs")).thenReturn(true);
        assertThrows(DuplicateAliasException.class, () -> service.shortenUrl(request("https://example.com", "docs")));
    }

    @Test
    void rejectsUrlsOutsideHttpAndHttps() {
        assertThrows(InvalidURLException.class, () -> service.shortenUrl(request("ftp://example.com", null)));
    }

    @Test
    void rejectsAnUnknownShortCode() {
        when(repository.findByShortCode("missing")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getUrl("missing"));
    }

    private ShortenUrlRequest request(String url, String alias) {
        ShortenUrlRequest request = new ShortenUrlRequest();
        request.setUrl(url);
        request.setCustomAlias(alias);
        return request;
    }
}

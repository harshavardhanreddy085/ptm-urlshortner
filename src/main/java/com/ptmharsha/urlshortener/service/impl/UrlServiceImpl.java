package com.ptmharsha.urlshortener.service.impl;

import com.ptmharsha.urlshortener.config.AppProperties;
import com.ptmharsha.urlshortener.dto.request.ShortenUrlRequest;
import com.ptmharsha.urlshortener.dto.response.ShortenUrlResponse;
import com.ptmharsha.urlshortener.entity.UrlMapping;
import com.ptmharsha.urlshortener.exception.DuplicateAliasException;
import com.ptmharsha.urlshortener.exception.InvalidURLException;
import com.ptmharsha.urlshortener.exception.LinkExpiredException;
import com.ptmharsha.urlshortener.exception.ResourceNotFoundException;
import com.ptmharsha.urlshortener.repository.UrlMappingRepository;
import com.ptmharsha.urlshortener.service.UrlService;
import com.ptmharsha.urlshortener.util.ShortCodeGenerator;
import com.ptmharsha.urlshortener.util.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UrlServiceImpl implements UrlService {

    private final UrlMappingRepository repository;
    private final ShortCodeGenerator generator;
    private final AppProperties appProperties;

    @Override
    public ShortenUrlResponse shortenUrl(ShortenUrlRequest request) {

        log.info("Creating short URL. url={}, alias={}",
                request.getUrl(),
                request.getCustomAlias());

        validateRequest(request);
        boolean hasCustomAlias = hasCustomAlias(request);
        validateCustomAlias(request.getCustomAlias());

        UrlMapping existing = repository
                .findByOriginalUrl(request.getUrl())
                .orElse(null);

        if (existing != null && !hasCustomAlias) {
            log.info("URL already exists.");
            return buildResponse(existing);
        }

        String shortCode = generateShortCode(request);

        UrlMapping entity = UrlMapping.builder()
                .originalUrl(request.getUrl())
                .shortCode(shortCode)
                .customAlias(request.getCustomAlias())
                .expiresAt(request.getExpiresAt())
                .clickCount(0L)
                .active(true)
                .build();

        UrlMapping saved = repository.save(entity);

        log.info("Saved URL mapping id={}, shortCode={}",
                saved.getId(),
                saved.getShortCode());

        return buildResponse(saved);
    }

    @Override
    @Transactional
    public UrlMapping getUrl(String shortCode) {

        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));

        validateMapping(mapping);

        return mapping;
    }

    private void validateMapping(UrlMapping mapping) {

        if (!mapping.getActive()) {
            throw new ResourceNotFoundException("Short URL is inactive");
        }

        if (mapping.getExpiresAt() != null &&
                LocalDateTime.now().isAfter(mapping.getExpiresAt())) {

            throw new LinkExpiredException("This link has expired");
        }

    }

    private String generateShortCode(ShortenUrlRequest request) {

        if (hasCustomAlias(request)) {

            return request.getCustomAlias();
        }

        String code;

        do {

            code = generator.generate();

        } while (repository.existsByShortCode(code));

        return code;
    }

    private void validateRequest(ShortenUrlRequest request) {

        if (!UrlValidator.isValid(request.getUrl())) {
            throw new InvalidURLException("Invalid URL");
        }

    }

    private void validateCustomAlias(String alias) {

        if (alias == null || alias.isBlank()) {
            return;
        }

        if (repository.existsByShortCode(alias)) {
            throw new DuplicateAliasException("Alias already exists");
        }

    }

    private boolean hasCustomAlias(ShortenUrlRequest request) {
        return request.getCustomAlias() != null
                && !request.getCustomAlias().isBlank();
    }

    private ShortenUrlResponse buildResponse(UrlMapping entity) {

        return ShortenUrlResponse.builder()
                .originalUrl(entity.getOriginalUrl())
                .shortCode(entity.getShortCode())
                .shortUrl(appProperties.getBaseUrl() + "/" + entity.getShortCode())
                .build();

    }
}

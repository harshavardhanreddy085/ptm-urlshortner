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
import com.ptmharsha.urlshortener.service.UrlMappingPersister;
import com.ptmharsha.urlshortener.service.UrlService;
import com.ptmharsha.urlshortener.util.ShortCodeGenerator;
import com.ptmharsha.urlshortener.util.UrlValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private final UrlMappingRepository repository;
    private final UrlMappingPersister persister;
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

        UrlMapping saved = hasCustomAlias
                ? saveCustomAlias(request)
                : saveGeneratedCode(request);

        log.info("Saved URL mapping id={}, shortCode={}",
                saved.getId(),
                saved.getShortCode());

        return buildResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
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

    private UrlMapping saveCustomAlias(ShortenUrlRequest request) {
        try {
            return persister.save(newMapping(request, request.getCustomAlias()));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateAliasException("Alias already exists");
        }
    }

    private UrlMapping saveGeneratedCode(ShortenUrlRequest request) {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            String candidate = generator.generate();

            if (repository.existsByShortCode(candidate)) {
                continue;
            }

            try {
                return persister.save(newMapping(request, candidate));
            } catch (DataIntegrityViolationException ex) {
                log.warn("Generated short code collided; retrying");
            }
        }

        throw new IllegalStateException("Could not allocate a unique short code");
    }

    private UrlMapping newMapping(ShortenUrlRequest request, String shortCode) {
        return UrlMapping.builder()
                .originalUrl(request.getUrl())
                .shortCode(shortCode)
                .customAlias(request.getCustomAlias())
                .expiresAt(request.getExpiresAt())
                .clickCount(0L)
                .active(true)
                .build();
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

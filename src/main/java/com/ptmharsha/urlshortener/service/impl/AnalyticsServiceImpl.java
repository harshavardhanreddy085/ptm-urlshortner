package com.ptmharsha.urlshortener.service.impl;

import com.ptmharsha.urlshortener.entity.UrlClick;
import com.ptmharsha.urlshortener.entity.UrlMapping;

import com.ptmharsha.urlshortener.repository.UrlClickRepository;
import com.ptmharsha.urlshortener.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl
        implements AnalyticsService {

    private final UrlClickRepository repository;

    @Override
    public void recordClick(
            UrlMapping mapping,
            HttpServletRequest request) {

        UrlClick click =
                UrlClick.builder()
                        .urlMapping(mapping)
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(
                                request.getHeader("User-Agent"))
                        .referer(
                                request.getHeader("Referer"))
                        .clickedAt(LocalDateTime.now())
                        .build();

        repository.save(click);

    }

}
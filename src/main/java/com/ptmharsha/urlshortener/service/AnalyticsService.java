package com.ptmharsha.urlshortener.service;

import com.ptmharsha.urlshortener.entity.UrlMapping;
import jakarta.servlet.http.HttpServletRequest;

public interface AnalyticsService {

    void recordClick(
            UrlMapping mapping,
            HttpServletRequest request);

}

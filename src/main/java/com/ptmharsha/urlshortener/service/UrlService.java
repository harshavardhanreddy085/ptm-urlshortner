package com.ptmharsha.urlshortener.service;

import com.ptmharsha.urlshortener.dto.request.ShortenUrlRequest;
import com.ptmharsha.urlshortener.dto.response.ShortenUrlResponse;
import com.ptmharsha.urlshortener.entity.UrlMapping;

public interface UrlService {

    ShortenUrlResponse shortenUrl(ShortenUrlRequest request);

    UrlMapping getUrl(String shortCode);

}
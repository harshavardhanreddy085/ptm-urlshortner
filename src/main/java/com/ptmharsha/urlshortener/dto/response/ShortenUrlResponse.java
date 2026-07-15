package com.ptmharsha.urlshortener.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShortenUrlResponse {

    private String originalUrl;

    private String shortCode;

    private String shortUrl;
}
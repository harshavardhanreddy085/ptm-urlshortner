package com.ptmharsha.urlshortener.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String baseUrl;

    private Cache cache = new Cache();

    @Getter
    @Setter
    public static class Cache {
        private long ttlHours = 24;
    }
}
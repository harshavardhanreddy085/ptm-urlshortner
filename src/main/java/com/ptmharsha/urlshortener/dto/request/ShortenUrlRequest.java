package com.ptmharsha.urlshortener.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShortenUrlRequest {

    @NotBlank(message = "url is required")
    private String url;

    @Size(min = 3, max = 30, message = "customAlias must be between 3 and 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "customAlias may contain only letters, digits, hyphens, and underscores")
    private String customAlias;

    private LocalDateTime expiresAt;
}

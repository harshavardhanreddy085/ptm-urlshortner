CREATE TABLE url_mapping
(
    id BIGSERIAL PRIMARY KEY,

    original_url TEXT NOT NULL,

    short_code VARCHAR(30) NOT NULL,

    custom_alias VARCHAR(30),

    click_count BIGINT NOT NULL DEFAULT 0,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    expires_at TIMESTAMP
);

ALTER TABLE url_mapping
ADD CONSTRAINT uk_short_code
UNIQUE (short_code);

ALTER TABLE url_mapping
ADD CONSTRAINT uk_custom_alias
UNIQUE (custom_alias);

CREATE INDEX idx_original_url
ON url_mapping(original_url);

CREATE INDEX idx_short_code
ON url_mapping(short_code);

CREATE INDEX idx_active
ON url_mapping(active);

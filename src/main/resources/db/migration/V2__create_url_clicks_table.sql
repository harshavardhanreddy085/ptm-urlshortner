CREATE TABLE url_clicks (

    id BIGSERIAL PRIMARY KEY,

    url_mapping_id BIGINT NOT NULL,

    ip_address VARCHAR(100),

    user_agent TEXT,

    referer TEXT,

    clicked_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_click_url
    FOREIGN KEY(url_mapping_id)
    REFERENCES url_mapping(id)

);
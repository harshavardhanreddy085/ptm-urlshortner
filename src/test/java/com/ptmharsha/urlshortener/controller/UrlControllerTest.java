package com.ptmharsha.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptmharsha.urlshortener.dto.request.ShortenUrlRequest;
import com.ptmharsha.urlshortener.dto.response.ShortenUrlResponse;
import com.ptmharsha.urlshortener.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateShortUrl() throws Exception {

        ShortenUrlRequest request = new ShortenUrlRequest();

        request.setUrl("https://google.com");

        ShortenUrlResponse response =
                ShortenUrlResponse.builder()
                        .originalUrl("https://google.com")
                        .shortCode("ABC1234")
                        .shortUrl("http://localhost:8081/ABC1234")
                        .build();

        when(urlService.shortenUrl(org.mockito.ArgumentMatchers.any()))
                .thenReturn(response);

        mockMvc.perform(
                        post("/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortCode")
                        .value("ABC1234"))
                .andExpect(jsonPath("$.originalUrl")
                        .value("https://google.com"));

    }

    @Test
    void shouldReturn400ForInvalidRequest() throws Exception {

        ShortenUrlRequest request =
                new ShortenUrlRequest();

        request.setUrl("");

        mockMvc.perform(
                        post("/shorten")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());

    }
}

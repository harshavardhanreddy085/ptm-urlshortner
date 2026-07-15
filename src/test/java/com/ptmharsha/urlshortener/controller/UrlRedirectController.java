package com.ptmharsha.urlshortener.controller;


import com.ptmharsha.urlshortener.service.UrlService;
import com.ptmharsha.urlshortener.entity.UrlMapping;
import com.ptmharsha.urlshortener.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(UrlRedirectController.class)
class UrlRedirectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UrlService urlService;

    @Test
    void shouldRedirect() throws Exception {

        when(urlService.getUrl("ABC1234"))
                .thenReturn(UrlMapping.builder()
                        .originalUrl("https://google.com")
                        .shortCode("ABC1234")
                        .build());

        mockMvc.perform(get("/ABC1234"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string(
                        "Location",
                        "https://google.com"));
    }

    @Test
    void shouldReturn404ForAnUnknownCode() throws Exception {
        when(urlService.getUrl("missing"))
                .thenThrow(new ResourceNotFoundException("Short URL not found"));

        mockMvc.perform(get("/missing"))
                .andExpect(status().isNotFound());
    }
}

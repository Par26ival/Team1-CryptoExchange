
package com.crypto.exchange.prices.controller;

import com.crypto.exchange.prices.dto.PricePointDTO;
import com.crypto.exchange.prices.service.PriceHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;
import org.springframework.http.MediaType;

@WebMvcTest(PriceHistoryController.class)
public class PriceHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceHistoryService priceHistoryService;

    @Test
    void shouldReturnHistoryForValidRange() throws Exception {
        List<PricePointDTO> mockResult = List.of(
                new PricePointDTO("2024-01-01T00:00:00Z", 31000.0),
                new PricePointDTO("2024-01-01T01:00:00Z", 31250.0)
        );

        when(priceHistoryService.getDownsampledHistory("BTC", "USD", "24h")).thenReturn(mockResult);

        mockMvc.perform(get("/api/prices/history/BTC")
                .param("fiatCode", "USD")
                .param("range", "24h"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("31000.0")))
                .andExpect(content().string(containsString("31250.0")));
    }

    @Test
    void shouldFailForInvalidRange() throws Exception {
        when(priceHistoryService.getDownsampledHistory("BTC", "USD", "2y"))
                .thenThrow(new IllegalArgumentException("Unsupported range: 2y"));

        mockMvc.perform(get("/api/prices/history/BTC")
                .param("fiatCode", "USD")
                .param("range", "2y"))
                .andExpect(status().is5xxServerError());
    }
}

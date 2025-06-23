
package com.crypto.exchange.prices.controller;

import com.crypto.exchange.prices.scheduler.PriceFetcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(PriceFetcherController.class)
public class PriceFetcherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceFetcher priceFetcher;

    @Test
    void shouldReturnPriceForValidTokenAndCurrency() throws Exception {
        when(priceFetcher.getPrice("BTC", "USD")).thenReturn(30750.25);

        mockMvc.perform(get("/api/prices/BTC/USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("30750.25"));
    }

    @Test
    void shouldReturnNotFoundForUnknownTokenOrCurrency() throws Exception {
        when(priceFetcher.getPrice("DOGE", "XYZ")).thenReturn(null);

        mockMvc.perform(get("/api/prices/DOGE/XYZ"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnNotFoundWhenFetcherReturnsNull() throws Exception {
        when(priceFetcher.getPrice("ETH", "USD")).thenReturn(null);

        mockMvc.perform(get("/api/prices/ETH/USD"))
                .andExpect(status().isNotFound());
    }
}

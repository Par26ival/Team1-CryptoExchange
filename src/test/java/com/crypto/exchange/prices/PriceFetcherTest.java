
package com.crypto.exchange.prices.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PriceFetcherTest {

    private PriceFetcher fetcher;

    @BeforeEach
    void setUp() {
        fetcher = new PriceFetcher();
    }

    @Test
    void testEmptyCacheReturnsNull() {
        Double price = fetcher.getPrice("BTC", "USD");
        assertNull(price);
    }

    @Test
    void testGetPriceReturnsCorrectValue() throws Exception {
        // Simулираме заредена стойност в кеша
        Field cacheField = PriceFetcher.class.getDeclaredField("priceCache");
        cacheField.setAccessible(true);

        Map<String, Map<String, Double>> mockCache = Map.of(
            "btc", Map.of("usd", 30123.45)
        );

        cacheField.set(fetcher, new java.util.concurrent.ConcurrentHashMap<>(mockCache));

        Double price = fetcher.getPrice("BTC", "USD");
        assertEquals(30123.45, price);
    }
}

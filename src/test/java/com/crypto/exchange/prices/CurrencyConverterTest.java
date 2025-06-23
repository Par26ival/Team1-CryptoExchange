
package com.crypto.exchange.prices.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class CurrencyConverterTest {

    @Test
    void testGetCoinGeckoId() {
        assertEquals("bitcoin", CurrencyConverter.getCoinGeckoId("BTC"));
        assertEquals("ethereum", CurrencyConverter.getCoinGeckoId("ETH"));
        assertEquals("usd", CurrencyConverter.getCoinGeckoId("USD"));
        assertEquals("bulgarian-lev", CurrencyConverter.getCoinGeckoId("BGN"));
        assertEquals("custom", CurrencyConverter.getCoinGeckoId("custom"));
    }

    @Test
    void testIsCrypto() {
        assertTrue(CurrencyConverter.isCrypto("BTC"));
        assertTrue(CurrencyConverter.isCrypto("ETH"));
        assertFalse(CurrencyConverter.isCrypto("USD"));
        assertFalse(CurrencyConverter.isCrypto("EUR"));
    }
}

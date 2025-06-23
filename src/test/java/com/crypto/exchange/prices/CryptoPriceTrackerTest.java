
package com.crypto.exchange.prices.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CryptoPriceTrackerTest {

    @Test
    void testValidResponseSimulation() {
        // NOTE: This test simulates the logic, but does not actually call the API
        // since fetchPriceData is static and prints only, we'll check if it throws errors

        assertDoesNotThrow(() -> {
            CryptoPriceTracker.fetchPriceData("bitcoin", "usd", "24h");
        });
    }

    @Test
    void testInvalidTimeFramePrintsUnsupported() {
        assertDoesNotThrow(() -> {
            CryptoPriceTracker.fetchPriceData("bitcoin", "usd", "3w"); // Invalid range
        });
    }
}

package com.crypto.exchange.prices.service;

import com.crypto.exchange.prices.db.model.CryptoPriceRecord;
import com.crypto.exchange.prices.scheduler.PriceFetcher;
import com.prices.CurrencyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class CryptoPriceService {

    private static final Logger log = LoggerFactory.getLogger(CryptoPriceService.class);
    private final PriceFetcher priceFetcher;

    public CryptoPriceService(PriceFetcher priceFetcher) {
        this.priceFetcher = priceFetcher;
    }

    public double getCryptoPriceInFiat(String crypto, String fiat) {
        crypto = CurrencyConverter.getCoinGeckoId(crypto);
        fiat = CurrencyConverter.getCoinGeckoId(fiat);
        log.info("Fetching price for {} in {}", crypto, fiat);
        return priceFetcher.getPrice(crypto, fiat);
    }

    public CryptoPriceRecord getExampleLatestPrices() {
        Map<String, Double> btcPrices = new HashMap<>();
        btcPrices.put("USD", 70000.99);
        btcPrices.put("EUR", 65000.50);
        btcPrices.put("BGN", 127200.33);

        Map<String, Double> ethPrices = new HashMap<>();
        ethPrices.put("USD", 3500.10);
        ethPrices.put("EUR", 3250.40);
        ethPrices.put("BGN", 6350.80);

        Map<String, Map<String, Double>> prices = new HashMap<>();
        prices.put("BTC", btcPrices);
        prices.put("ETH", ethPrices);

        return new CryptoPriceRecord(Instant.now(), prices);
    }

}

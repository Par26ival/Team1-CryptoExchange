package com.crypto.exchange.prices.service;

import com.crypto.exchange.prices.db.CryptoPriceRepository;
import com.crypto.exchange.prices.db.model.CryptoPriceRecord;
import com.example.prices.PriceFetcher;
import com.prices.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoPriceService {

    //CryptoPriceRepository cryptoPriceRepository;
    @Autowired
    PriceFetcher priceFetcher;

    public double getCryptoPriceInFiat(String crypto, String fiat) {
        crypto = CurrencyConverter.getCoinGeckoId(crypto);
        fiat = CurrencyConverter.getCoinGeckoId(fiat);
        log.info("Fetching price for {} in {}", crypto, fiat);
        Double price = priceFetcher.getPrice(crypto, fiat);
        return price != null ? price : -1.0;
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


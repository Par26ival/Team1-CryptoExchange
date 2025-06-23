package com.crypto.exchange.prices.service;

import com.crypto.exchange.prices.dto.PricePointDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CryptoPriceService {
    public Map<String, Map<String, Double>> getAllLatestPrices() {
        return new HashMap<>();
    }

    public List<PricePointDTO> getPriceHistory(String crypto, String fiat, String from, String to) {
        return new ArrayList<>();
    }

    public Double convert(String from, String to, double amount) {
        return null;
    }

    public Double getCryptoPriceInFiat(String crypto, String fiat) {
        return null;
    }
}
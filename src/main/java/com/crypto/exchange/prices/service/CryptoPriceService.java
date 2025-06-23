package com.crypto.exchange.prices.service;

import com.crypto.exchange.prices.dto.PricePointDTO;
import com.crypto.exchange.prices.projection.DownsampledPriceProjection;
import com.crypto.exchange.prices.repository.PriceHistoryRepository;
import com.crypto.exchange.prices.scheduler.PriceFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CryptoPriceService {
    @Autowired
    private PriceHistoryRepository priceHistoryRepository;
    @Autowired
    private PriceFetcher priceFetcher;

    public Map<String, Map<String, Double>> getAllLatestPrices() {
        return priceFetcher.getAllLatestPrices();
    }

    public List<PricePointDTO> getPriceHistory(String crypto, String fiat, String bucketSize, String rangeInterval) {
        List<DownsampledPriceProjection> projections = priceHistoryRepository.getDownsampledPrices(
                crypto, fiat, bucketSize, rangeInterval);
        List<PricePointDTO> history = new ArrayList<>();
        for (DownsampledPriceProjection proj : projections) {
            history.add(new PricePointDTO(proj.getBucket(), proj.getAvgPrice()));
        }
        return history;
    }

    public Double convert(String from, String to, double amount) {
        Double fromPriceInUSD = getCryptoPriceInFiat(from, "usd");
        Double toPriceInUSD = getCryptoPriceInFiat(to, "usd");
        if (fromPriceInUSD == null || toPriceInUSD == null)
            return null;
        double usdValue = amount * fromPriceInUSD;
        return usdValue / toPriceInUSD;
    }

    public Double getCryptoPriceInFiat(String crypto, String fiat) {
        return priceFetcher.getPrice(crypto, fiat);
    }
}
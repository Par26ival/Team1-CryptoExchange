package com.crypto.exchange.prices.service;

import com.crypto.exchange.prices.model.PriceEntity;
import com.crypto.exchange.prices.repository.PriceHistoryRepository;
import com.crypto.exchange.prices.dto.PricePointDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceHistoryService {
    public final PriceHistoryRepository priceHistoryRepository;

    public List<PricePointDTO> getDownsampledHistory(String cryptoCode, String fiatCode, String range) {
        record Pair(String bucketSize, String rangeInterval) {}
        Pair p = switch (range) {
            case "1h"  -> new Pair("1 minute",    "1 hour");
            case "6h"  -> new Pair("10 minutes","6 hours");
            case "24h" -> new Pair("10 minutes","24 hours");
            case "7d"  -> new Pair("1 hour",      "7 days");
            case "30d" -> new Pair("4 hours",   "30 days");
            case "1y"  -> new Pair("1 day",       "1 year");
            default    -> throw new IllegalArgumentException("Unsupported range: " + range);
        };

        log.info("range={}, bucket={}, span={}", range, p.bucketSize(), p.rangeInterval());

        return priceHistoryRepository.getDownsampledPrices(cryptoCode, fiatCode, p.bucketSize(), p.rangeInterval())
                .stream()
                .map(pr -> new PricePointDTO(pr.getBucket(), pr.getAvgPrice()))
                .toList();
    }

    // Seeds DB in the background on a separate thread.
    // 70 seconds per coin, because of CoinGecko's rate limit.
    @Async
    @EventListener(ApplicationReadyEvent.class)
    void seedDatabase() {

        record Coin(String id, String symbol) {}
        List<Coin> coins = List.of(
                new Coin("bitcoin",      "BTC"),
                new Coin("ethereum",     "ETH"),
                new Coin("binancecoin",  "BNB"),
                new Coin("solana",       "SOL"),
                new Coin("polygon",      "MATIC"),
                new Coin("avalanche-2",  "AVAX"),
                new Coin("tether",       "USDT"),
                new Coin("usd-coin",     "USDC"),
                new Coin("dai",          "DAI"),
                new Coin("chainlink",    "LINK"),
                new Coin("uniswap",      "UNI"),
                new Coin("aave",         "AAVE")
        );

        int[] spans = {365, 30, 1};

        for (Coin c : coins) {
            for (int days : spans) {
                try {
                    log.info("Seeding {} {} ({} days)", c.id(), c.symbol(), days);
                    fetchAndSave(c.id(), c.symbol(), days);
                } catch (Exception ex) {
                    log.warn("Seed failed for {} ({} days): {}", c.id(), days, ex.getMessage());
                }
            }

            try {
                Thread.sleep(1000 * 70);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fetchAndSave(String coinId, String symbol, int days) throws IOException, InterruptedException {
        String url = String.format(
                "https://api.coingecko.com/api/v3/coins/%s/market_chart?vs_currency=usd&days=%d", coinId, days);

        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        String body = client.send(req, HttpResponse.BodyHandlers.ofString()).body();

        Map<?, ?> json = new ObjectMapper().readValue(body, Map.class);
        List<List<Object>> prices = (List<List<Object>>) json.get("prices");

        for (List<Object> p : prices) {
            priceHistoryRepository.saveIgnore(
                    symbol,
                    "USD",
                    new BigDecimal(p.get(1).toString()),
                    Instant.ofEpochMilli(((Number) p.get(0)).longValue()));
        }
    }
}

package com.crypto.exchange.prices.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Scanner;

@Component
public class PriceFetcher {

    private static final Logger logger = LoggerFactory.getLogger(PriceFetcher.class);

    private static final String COINGECKO_API = "https://api.coingecko.com/api/v3/simple/price";
    private static final String BINANCE_API = "https://api.binance.com/api/v3/ticker/price";
    private static final String CACHE_NAME   = "latest-prices";

    private final ObjectMapper mapper;
    private final Cache priceCache;
    private volatile long lastUpdateTimestamp = 0;

    public PriceFetcher(ObjectMapper mapper, CacheManager cacheManager) {
        this.mapper = mapper;
        this.priceCache = cacheManager.getCache(CACHE_NAME);
        if (priceCache == null) {
            throw new IllegalStateException("Cache '" + CACHE_NAME + "' not configured");
        }
    }

    @PostConstruct
    public void init() {
        logger.info("PriceFetcher initialized and first fetch will happen shortly.");
    }

    @PreDestroy
    public void shutdown() {
        logger.info("PriceFetcher shutting down.");
    }

    public Double getPrice(String tokenId, String fiatCurrency) {
        Map<String, Double> tokenPrices = priceCache.get(tokenId.toLowerCase(), Map.class);
        if (tokenPrices != null) {
            return tokenPrices.get(fiatCurrency.toLowerCase());
        }
        return null;
    }

    @Scheduled(fixedRate = 60000)
    public void updatePrices() {
        boolean success = fetchFromCoinGecko();
        if (!success) {
            logger.warn("Falling back to Binance (partial support)");
            fetchFromBinance();
        }
    }

    private boolean fetchFromCoinGecko() {
        try {
            String ids = "bitcoin,ethereum,binancecoin,solana,polygon,avalanche-2,tether,usd-coin,dai,chainlink,uniswap,aave";
            String vsCurrencies = "usd,eur,bgn";
            String urlString = String.format("%s?ids=%s&vs_currencies=%s", COINGECKO_API, ids, vsCurrencies);

            HttpURLConnection conn = (HttpURLConnection) java.net.URI.create(urlString).toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();

            if (conn.getResponseCode() != 200) {
                logger.error("CoinGecko API failed with HTTP code: {}", conn.getResponseCode());
                return false;
            }

            Map<String, Map<String, Double>> prices;
            try (var in = conn.getInputStream()) {
                prices = mapper.readValue(in, new TypeReference<>() {});
            }
            prices.forEach(priceCache::put);

            lastUpdateTimestamp = System.currentTimeMillis();
            logger.info("Prices refreshed from CoinGecko: {} symbols (at {})", prices.size(), lastUpdateTimestamp);
            return true;

        } catch (IOException e) {
            logger.error("Error fetching prices from CoinGecko", e);
            return false;
        }
    }

    private void fetchFromBinance() {
        try {
            String[] symbols = {"BTCUSDT", "ETHUSDT", "BNBUSDT"};
            for (String symbol : symbols) {
                String urlString = BINANCE_API + "?symbol=" + symbol;
                HttpURLConnection conn = (HttpURLConnection) java.net.URI.create(urlString).toURL().openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.connect();

                if (conn.getResponseCode() != 200) {
                    logger.warn("Binance API failed for symbol {}", symbol);
                    continue;
                }

                StringBuilder inline = new StringBuilder();
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    while (scanner.hasNext()) {
                        inline.append(scanner.nextLine());
                    }
                }

                Map<String, Object> response = mapper.readValue(inline.toString(), new TypeReference<>() {});
                String tokenId = symbol.substring(0, symbol.length() - 4).toLowerCase(); // e.g., btc
                Double price = Double.parseDouble((String) response.get("price"));
                priceCache.put(tokenId, Map.of("usd", price));
            }

            logger.info("Prices updated from Binance fallback");

        } catch (IOException e) {
            logger.error("Error fetching prices from Binance fallback", e);
        }
    }
}

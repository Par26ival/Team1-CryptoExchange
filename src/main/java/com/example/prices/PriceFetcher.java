package com.example.prices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PriceFetcher {

    private static final Logger logger = Logger.getLogger(PriceFetcher.class.getName());

    private static final String COINGECKO_API = "https://api.coingecko.com/api/v3/simple/price";
    private static final String BINANCE_API = "https://api.binance.com/api/v3/ticker/price";
    private static final int CACHE_EXPIRY_SECONDS = 60;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ObjectMapper mapper = new ObjectMapper();

    private final ConcurrentMap<String, Map<String, Double>> priceCache = new ConcurrentHashMap<>();
    private volatile long lastUpdateTimestamp = 0;

    public PriceFetcher() {
        scheduler.scheduleAtFixedRate(this::updatePrices, 0, CACHE_EXPIRY_SECONDS, TimeUnit.SECONDS);
    }

    public Double getPrice(String tokenId, String fiatCurrency) {
        Map<String, Double> tokenPrices = priceCache.get(tokenId.toLowerCase());
        if (tokenPrices != null) {
            return tokenPrices.get(fiatCurrency.toLowerCase());
        }
        return null;
    }

    private void updatePrices() {
        boolean success = fetchFromCoinGecko();
        if (!success) {
            logger.warning("Falling back to Binance (partial support)");
            fetchFromBinance(); // Optional fallback
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
                logger.severe("CoinGecko API failed with HTTP code: " + conn.getResponseCode());
                return false;
            }

            StringBuilder inline = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
            }

            Map<String, Map<String, Double>> prices = mapper.readValue(inline.toString(),
                    new TypeReference<>() {
                    });

            priceCache.clear();
            priceCache.putAll(prices);
            lastUpdateTimestamp = System.currentTimeMillis();
            logger.info("Prices updated from CoinGecko at " + lastUpdateTimestamp);
            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error fetching prices from CoinGecko", e);
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
                    logger.warning("Binance API failed for symbol " + symbol);
                    continue;
                }

                StringBuilder inline = new StringBuilder();
                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    while (scanner.hasNext()) {
                        inline.append(scanner.nextLine());
                    }
                }

                Map<String, Object> response = mapper.readValue(inline.toString(),
                        new TypeReference<>() {
                        });

                String tokenId = symbol.substring(0, symbol.length() - 4).toLowerCase(); // e.g., btc
                Double price = Double.parseDouble((String) response.get("price"));
                priceCache.put(tokenId, Map.of("usd", price));
            }

            logger.info("Prices updated from Binance as fallback");

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error fetching prices from Binance fallback", e);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        PriceFetcher fetcher = new PriceFetcher();

        Thread.sleep(5000);

        System.out.println("ETH price in USD: " + fetcher.getPrice("ethereum", "usd"));
        System.out.println("BTC price in EUR: " + fetcher.getPrice("bitcoin", "eur"));

        fetcher.shutdown();
    }
}
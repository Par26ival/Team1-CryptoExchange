package com.example.prices;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class PriceFetcher {

    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price";
    private static final int CACHE_EXPIRY_SECONDS = 60; // cache prices for 60 seconds

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ObjectMapper mapper = new ObjectMapper();

    private volatile Map<String, Map<String, Double>> priceCache; // token -> (currency -> price)
    private volatile long lastUpdateTimestamp = 0;

    public PriceFetcher() {
        // Start scheduled price updates every CACHE_EXPIRY_SECONDS
        scheduler.scheduleAtFixedRate(this::updatePrices, 0, CACHE_EXPIRY_SECONDS, TimeUnit.SECONDS);
    }

    // Call this method to get price of a token in a specific fiat currency
    // Example: getPrice("bitcoin", "usd") returns current price of Bitcoin in USD
    public Double getPrice(String tokenId, String fiatCurrency) {
        if (priceCache == null) {
            updatePrices(); // force update if cache is empty
        }
        Map<String, Double> tokenPrices = priceCache.get(tokenId.toLowerCase());
        if (tokenPrices != null) {
            return tokenPrices.get(fiatCurrency.toLowerCase());
        }
        return null; // price not found
    }

    private void updatePrices() {
        try {
            String ids = "bitcoin,ethereum,binancecoin,solana,polygon,avalanche-2,tether,usd-coin,dai,chainlink,uniswap,aave";
            String vsCurrencies = "usd,eur,bgn";

            String urlString = String.format("%s?ids=%s&vs_currencies=%s", API_URL, ids, vsCurrencies);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if(responseCode != 200) {
                System.err.println("Failed to fetch prices, HTTP response: " + responseCode);
                return;
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder inline = new StringBuilder();
            while(scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();

            // Parse JSON response
            Map<String, Map<String, Double>> prices = mapper.readValue(inline.toString(),
                    new TypeReference<Map<String, Map<String, Double>>>() {});

            priceCache = prices;
            lastUpdateTimestamp = System.currentTimeMillis();
            System.out.println("Prices updated at " + lastUpdateTimestamp);

        } catch (IOException e) {
            System.err.println("Exception while fetching prices: " + e.getMessage());
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        PriceFetcher fetcher = new PriceFetcher();

        // Wait some time to let first update happen
        Thread.sleep(2000);

        // Example usage:
        System.out.println("ETH price in USD: " + fetcher.getPrice("ethereum", "usd"));
        System.out.println("BTC price in EUR: " + fetcher.getPrice("bitcoin", "eur"));

        // Remember to shutdown when app closes
        fetcher.shutdown();
    }
}

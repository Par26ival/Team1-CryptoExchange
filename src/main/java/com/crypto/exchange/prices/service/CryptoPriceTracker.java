package com.crypto.exchange.prices.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class CryptoPriceTracker {

    private static final String API_URL = "https://api.coingecko.com/api/v3/coins/";

    public static void fetchPriceData(String coinId, String currency, String timeFrame)
            throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + coinId
                        + "?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject json = new JSONObject(response.body());

        JSONObject marketData = json.getJSONObject("market_data");
        double currentPrice = marketData.getJSONObject("current_price").getDouble(currency.toLowerCase());
        double changePercent = 0;

        switch (timeFrame) {
            case "24h":
                changePercent = marketData.getDouble("price_change_percentage_24h");
                break;
            case "7d":
                changePercent = marketData.getDouble("price_change_percentage_7d");
                break;
            case "30d":
                changePercent = marketData.getDouble("price_change_percentage_30d");
                break;
            case "1y":
                changePercent = marketData.getDouble("price_change_percentage_1y");
                break;
            default:
                System.out.println("Unsupported timeframe.");
                return;
        }

        double previousPrice = currentPrice / (1 + (changePercent / 100));
        System.out.printf("Live Price of %s: %.2f %s\n", coinId, currentPrice, currency.toUpperCase());
        System.out.printf("Price %s ago: %.2f %s\n", timeFrame, previousPrice, currency.toUpperCase());
        System.out.printf("Change: %.2f%%\n", changePercent);
    }

    public static void main(String[] args) {
        try {

            fetchPriceData("bitcoin", "usd", "7d");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

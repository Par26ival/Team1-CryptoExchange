package com.prices;

import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class CurrencyConverter {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://api.coingecko.com/api/v3/simple/price";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter amount to convert: ");
        BigDecimal amount = scanner.nextBigDecimal();

        System.out.print("Enter source currency (e.g., BTC, USD): ");
        String from = scanner.next().toLowerCase();

        System.out.print("Enter target currency (e.g., ETH, EUR): ");
        String to = scanner.next().toLowerCase();

        try {
            BigDecimal rate = fetchExchangeRate(from, to);
            if (rate != null) {
                BigDecimal result = amount.multiply(rate);

                int precision = (isCrypto(to) ? 8 : 2);
                result = result.setScale(precision, RoundingMode.HALF_UP);

                System.out.println("Converted amount: " + result + " " + to.toUpperCase());
            } else {
                System.out.println("Conversion not available for selected currencies.");
            }
        } catch (IOException e) {
            System.out.println("Failed to fetch exchange rate: " + e.getMessage());
        }

        scanner.close();
    }

    private static BigDecimal fetchExchangeRate(String from, String to) throws IOException {
        String url = API_URL + "?ids=" + getCoinGeckoId(from) + "&vs_currencies=" + to;

        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                return null;

            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

            String id = getCoinGeckoId(from);
            if (!json.has(id))
                return null;

            JsonObject rates = json.getAsJsonObject(id);
            if (!rates.has(to))
                return null;

            return rates.get(to).getAsBigDecimal();
        }
    }

    public static String getCoinGeckoId(String symbol) {
        return switch (symbol.toUpperCase()) {
            case "BTC" -> "bitcoin";
            case "ETH" -> "ethereum";
            case "USD" -> "usd";
            case "EUR" -> "eur";
            case "BGN" -> "bulgarian-lev";
            default -> symbol.toLowerCase(); 
        };
    }

    private static boolean isCrypto(String currency) {
        return switch (currency.toUpperCase()) {
            case "BTC", "ETH" -> true;
            default -> false;
        };
    }
}
package com.crypto.exchange.prices.db.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "crypto_prices")
@NoArgsConstructor
@Getter
@Setter
public class CryptoPriceRecord {
    @Id
    private String id;

    private Instant timestamp;
    private Map<String, Map<String, Double>> prices; // e.g. prices.get("BTC").get("USD")

    public CryptoPriceRecord(Instant timestamp, Map<String, Map<String, Double>> prices) {
        this.timestamp = timestamp;
        this.prices = prices;
    }

}
package com.crypto.exchange.prices.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "crypto_prices")
@Getter
@Setter
public class PriceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cryptoCode; // e.g., BTC, ETH
    private String fiatCode; // e.g., USD
    private BigDecimal price;
    private Instant timestamp;
}

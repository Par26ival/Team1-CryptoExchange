package com.crypto.exchange.prices.dto;

import java.time.Instant;

public record PricePointDTO (Instant timestamp, Double price) {
}

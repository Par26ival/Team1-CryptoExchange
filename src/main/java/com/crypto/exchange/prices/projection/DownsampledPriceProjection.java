package com.crypto.exchange.prices.projection;

import java.time.Instant;

public interface DownsampledPriceProjection {
    Instant getBucket();
    Double getAvgPrice();
}

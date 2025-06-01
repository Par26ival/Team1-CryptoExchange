package com.crypto.exchange.prices.db.projection;

import java.time.Instant;

public interface DownsampledPriceProjection {
    Instant getBucket();
    Double getAvgPrice();
}

package com.crypto.exchange.prices;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {
    private static final int MAX_REQUESTS_PER_SECOND = 50;
    private final AtomicInteger requestCount = new AtomicInteger(0);

    public RateLimiter() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                () -> requestCount.set(0), 1, 1, TimeUnit.SECONDS);
    }

    public void checkRateLimit() {
        if (requestCount.incrementAndGet() > MAX_REQUESTS_PER_SECOND) {
            throw new RateLimitExceededException("Too many requests - rate limit exceeded");
        }
    }
}
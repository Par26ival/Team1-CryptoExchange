package com.crypto.exchange.prices.controller.config;

import com.crypto.exchange.prices.scheduler.PriceFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceFetcherConfig {
    @Bean
    public PriceFetcher priceFetcher() {
        return new PriceFetcher();
    }

}
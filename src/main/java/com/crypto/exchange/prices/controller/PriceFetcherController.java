package com.crypto.exchange.prices.controller;

import com.crypto.exchange.prices.scheduler.PriceFetcher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prices")
public class PriceFetcherController {

    private final PriceFetcher priceFetcher;

    public PriceFetcherController(PriceFetcher priceFetcher) {
        this.priceFetcher = priceFetcher;
    }

    @GetMapping("/{token}/{currency}")
    public ResponseEntity<Double> getPrice(
            @PathVariable String token,
            @PathVariable String currency
    ) {
        Double price = priceFetcher.getPrice(token, currency);
        if (price != null) {
            return ResponseEntity.ok(price);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

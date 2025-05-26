package com.crypto.exchange.prices.controller;

import com.crypto.exchange.prices.db.model.CryptoPriceRecord;
import com.crypto.exchange.prices.service.CryptoPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto-prices")
@RequiredArgsConstructor
public class CryptoPriceController {

    private final CryptoPriceService cryptoPriceService;

//    @GetMapping("/latest") // returns everything
//    public CryptoPriceRecord getLatestPrices() {
//        return cryptoPriceService.getLatestPrices();
//    }

    @GetMapping("/{crypto}/{fiat}")
    public ResponseEntity<Double> getCryptoPriceInFiat(@PathVariable String crypto, @PathVariable String fiat) {
        Double price = cryptoPriceService.getCryptoPriceInFiat(crypto, fiat);
        if (price != null) {
            return ResponseEntity.ok(price);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}


package com.crypto.exchange.prices.controller;

import com.crypto.exchange.prices.dto.PricePointDTO;
import com.crypto.exchange.prices.service.CryptoPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/crypto-prices")
public class PriceFetcherController {

    private final CryptoPriceService cryptoPriceService;

    public PriceFetcherController(CryptoPriceService cryptoPriceService) {
        this.cryptoPriceService = cryptoPriceService;
    }

    // 1. Get all latest prices for all supported cryptos
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Map<String, Double>>> getAllLatestPrices() {
        return ResponseEntity.ok(cryptoPriceService.getAllLatestPrices());
    }

    // 2. Get price history for a crypto/fiat pair
    @GetMapping("/history/{crypto}/{fiat}")
    public ResponseEntity<List<PricePointDTO>> getPriceHistory(
            @PathVariable String crypto,
            @PathVariable String fiat,
            @RequestParam String from,
            @RequestParam String to) {
        return ResponseEntity.ok(cryptoPriceService.getPriceHistory(crypto, fiat, from, to));
    }

    // 3. Convert between two currencies
    @GetMapping("/convert")
    public ResponseEntity<Double> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam double amount) {
        Double result = cryptoPriceService.convert(from, to, amount);
        return result != null ? ResponseEntity.ok(result) : ResponseEntity.badRequest().build();
    }

    // 4. Get latest price for a crypto in a fiat currency
    @GetMapping("/{crypto}/{fiat}")
    public ResponseEntity<Double> getCryptoPriceInFiat(@PathVariable String crypto, @PathVariable String fiat) {
        Double price = cryptoPriceService.getCryptoPriceInFiat(crypto, fiat);
        return price != null ? ResponseEntity.ok(price) : ResponseEntity.notFound().build();
    }
}

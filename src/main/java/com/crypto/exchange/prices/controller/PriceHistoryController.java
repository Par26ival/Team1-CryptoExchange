package com.crypto.exchange.prices.controller;


import com.crypto.exchange.prices.dto.PricePointDTO;
import com.crypto.exchange.prices.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/prices/history")
@RequiredArgsConstructor
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    @GetMapping
    public List<PricePointDTO> getPriceHistory(
            @RequestParam String cryptoCode,
            @RequestParam(defaultValue = "USD") String fiatCode,
            @RequestParam(defaultValue = "24h") String range
    ) {
        return priceHistoryService.getDownsampledHistory(cryptoCode, fiatCode, range);
    }
}

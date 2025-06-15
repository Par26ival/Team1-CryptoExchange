package com.crypto.exchange.prices.controller;


import com.crypto.exchange.prices.dto.PricePointDTO;
import com.crypto.exchange.prices.service.PriceHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prices/history")
@RequiredArgsConstructor
@Slf4j
public class PriceHistoryController {
    private final PriceHistoryService priceHistoryService;

    @GetMapping("/{cryptoCode}")
    public List<PricePointDTO> getPriceHistory(
            @PathVariable String cryptoCode,
            @RequestParam(defaultValue = "USD") String fiatCode,
            @RequestParam(defaultValue = "24h") String range
    ) {
        log.info("In price history controller");
        return priceHistoryService.getDownsampledHistory(cryptoCode, fiatCode, range);
    }
}

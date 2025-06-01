package com.crypto.exchange.prices.service;

import com.crypto.exchange.prices.db.repository.PriceHistoryRepository;
import com.crypto.exchange.prices.dto.PricePointDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceHistoryService {
    public final PriceHistoryRepository priceHistoryRepository;

    public List<PricePointDTO> getDownsampledHistory(String cryptoCode, String fiatCode, String range) {
        String interval = switch (range) {
            case "1h" -> "minute";
            case "6h", "24h" -> "10 minutes";
            case "7d" -> "hour";
            case "30d" -> "4 hours";
            case "1y" -> "day";
            default -> throw new IllegalArgumentException("Unsupported range: " + range);
        };

        return priceHistoryRepository.getDownsampledPrices(cryptoCode, fiatCode, interval, range).stream()
                .map(p -> new PricePointDTO(p.getBucket(), p.getAvgPrice()))
                .toList();
    }
}

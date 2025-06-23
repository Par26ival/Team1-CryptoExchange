
package com.crypto.exchange.prices.service;

import com.crypto.exchange.prices.dto.PricePointDTO;
import com.crypto.exchange.prices.projection.DownsampledPriceProjection;
import com.crypto.exchange.prices.repository.PriceHistoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PriceHistoryServiceTest {

    private final PriceHistoryRepository repository = mock(PriceHistoryRepository.class);
    private final PriceHistoryService service = new PriceHistoryService(repository);

    @Test
    void shouldReturnDtoListWithCorrectMapping() {
        DownsampledPriceProjection proj1 = mock(DownsampledPriceProjection.class);
        DownsampledPriceProjection proj2 = mock(DownsampledPriceProjection.class);

        when(proj1.getBucket()).thenReturn(Instant.parse("2024-06-01T10:00:00Z"));
        when(proj1.getAvgPrice()).thenReturn(30000.0);
        when(proj2.getBucket()).thenReturn(Instant.parse("2024-06-01T11:00:00Z"));
        when(proj2.getAvgPrice()).thenReturn(31000.0);

        when(repository.getDownsampledPrices("BTC", "USD", "10 minutes", "24h"))
                .thenReturn(List.of(proj1, proj2));

        List<PricePointDTO> result = service.getDownsampledHistory("BTC", "USD", "24h");

        assertEquals(2, result.size());
        assertEquals(30000.0, result.get(0).price());
        assertEquals(31000.0, result.get(1).price());
    }

    @Test
    void shouldThrowOnInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.getDownsampledHistory("BTC", "USD", "8w");
        });
    }
}

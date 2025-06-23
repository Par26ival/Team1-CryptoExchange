
package com.crypto.exchange.prices.repository;

import com.crypto.exchange.prices.model.PriceEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PriceHistoryRepositoryTest {

    @Autowired
    private PriceHistoryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldStoreAndQueryPriceData() {
        PriceEntity p1 = new PriceEntity();
        p1.setCryptoCode("BTC");
        p1.setFiatCode("USD");
        p1.setPrice(java.math.BigDecimal.valueOf(30000));
        p1.setTimestamp(Instant.now().minusSeconds(3600));

        PriceEntity p2 = new PriceEntity();
        p2.setCryptoCode("BTC");
        p2.setFiatCode("USD");
        p2.setPrice(java.math.BigDecimal.valueOf(32000));
        p2.setTimestamp(Instant.now().minusSeconds(1800));

        entityManager.persist(p1);
        entityManager.persist(p2);
        entityManager.flush();

        List<?> result = repository.getDownsampledPrices("BTC", "USD", "hour", "24h");
        assertFalse(result.isEmpty());
    }
}

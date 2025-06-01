package com.crypto.exchange.prices.db.repository;

import com.crypto.exchange.prices.db.model.PriceEntity;
import com.crypto.exchange.prices.db.projection.DownsampledPriceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<PriceEntity, Long> {

    @Query(value = """
        SELECT 
          date_trunc(:interval, timestamp) AS bucket,
          AVG(price) AS avg_price
        FROM crypto_prices
        WHERE crypto_code = :cryptoCode
          AND fiat_code = :fiatCode
          AND timestamp >= NOW() - CAST(:range AS INTERVAL)
        GROUP BY bucket
        ORDER BY bucket
    """, nativeQuery = true)
    List<DownsampledPriceProjection> getDownsampledPrices(
            @Param("cryptoCode") String cryptoCode,
            @Param("fiatCode") String fiatCode,
            @Param("interval") String interval,
            @Param("range") String range
    );
}

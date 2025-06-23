package com.crypto.exchange.prices.repository;

import com.crypto.exchange.prices.model.PriceEntity;
import com.crypto.exchange.prices.projection.DownsampledPriceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO crypto_prices
               (crypto_code, fiat_code, price, timestamp)
        VALUES (:crypto, :fiat, :price, :ts)
        ON CONFLICT ON CONSTRAINT ux_price
        DO NOTHING
    """, nativeQuery = true)
    void saveIgnore(@Param("crypto") String crypto,
                    @Param("fiat")   String fiat,
                    @Param("price") BigDecimal price,
                    @Param("ts") Instant ts);


    @Query(value = """
        SELECT
           date_bin( CAST(:bucketSize AS interval), timestamp, TIMESTAMP '1970-01-01') AS bucket,
          AVG(price) AS avg_price
        FROM crypto_prices
        WHERE crypto_code = :cryptoCode
          AND fiat_code = :fiatCode
          AND timestamp >= NOW() - CAST(:rangeInterval AS interval)
        GROUP BY bucket
        ORDER BY bucket
    """, nativeQuery = true)
    List<DownsampledPriceProjection> getDownsampledPrices(
            @Param("cryptoCode") String cryptoCode,
            @Param("fiatCode") String fiatCode,
            @Param("bucketSize") String interval,
            @Param("rangeInterval") String rangeInterval
    );
}

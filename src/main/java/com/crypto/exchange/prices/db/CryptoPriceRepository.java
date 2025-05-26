package com.crypto.exchange.prices.db;

import com.crypto.exchange.prices.db.model.CryptoPriceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CryptoPriceRepository extends MongoRepository<CryptoPriceRecord, String> {}


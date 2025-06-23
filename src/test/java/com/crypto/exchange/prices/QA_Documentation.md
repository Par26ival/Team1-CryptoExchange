
## QA Documentation – Crypto Price Tracker Microservice

### 1. Introduction
The Crypto Price Tracker microservice is part of a system for tracking real-time and historical cryptocurrency prices. It retrieves data from external APIs (CoinGecko and Binance), stores historical prices in a PostgreSQL database, and exposes endpoints for clients to retrieve both cached and historical price data. The microservice also supports currency conversion and scheduled price updates.

This document outlines the functionalities of the system, validation mechanisms, edge cases, and the testing strategy applied throughout development.

### 2. Functionalities
- Fetch current prices from cache
- Fetch historical price data downsampled by time interval
- Convert values between different crypto and fiat currencies
- Periodically update price cache using scheduled jobs
- Fallback to Binance API when CoinGecko is unavailable

### 3. API Endpoints and Logic Mapping

GET /api/prices/{token}/{currency}
- Description: Returns the current price for a token in the specified currency
- Returns:
    - 200 OK with a double value
    - 404 Not Found if price is not available

GET /api/prices/history/{cryptoCode}?fiatCode=USD&range=24h
- Description: Returns downsampled historical price data
- Returns:
    - 200 OK with a list of timestamp-price pairs
    - 500 Internal Server Error if range is unsupported

### 4. Data Model

Table: crypto_prices
- id (Long)
- crypto_code (String)
- fiat_code (String)
- price (BigDecimal)
- timestamp (Instant)

### 5. Internal Components

PriceFetcher
- Scheduled every 60 seconds to fetch live prices
- Uses CoinGecko by default
- Falls back to Binance if CoinGecko fails
- Stores results in an internal cache

PriceHistoryService
- Maps time ranges to SQL-friendly intervals
- Queries historical prices with aggregation
- Maps results to PricePointDTO

CurrencyConverter
- Fetches real-time exchange rates from CoinGecko
- Supports both crypto and fiat
- Applies different precision rules depending on type

### 6. Validation and Errors

Price fetch endpoint:
- Case-insensitive input for token and currency
- Returns 404 if price is not found

Price history endpoint:
- Validates the time range string (1h, 24h, 7d, 30d, 1y)
- Throws IllegalArgumentException on unsupported range

CurrencyConverter:
- Validates and maps symbols for API usage
- Returns null on unknown conversion paths

### 7. Testing Strategy

Unit Tests:
- PriceFetcherControllerTest – tests return values for known and unknown tokens
- PriceHistoryControllerTest – tests valid and invalid range values
- PriceHistoryServiceTest – tests mapping and interval logic
- CurrencyConverterTest – tests currency ID mapping and crypto recognition
- CryptoPriceTrackerTest – tests API logic behavior
- PriceFetcherTest – tests internal caching behavior with reflection
- PriceHistoryRepositoryTest – tests native query with H2 database

Integration Style:
- Controllers tested with MockMvc
- Repository tested with H2 and @DataJpaTest
- Caching logic verified without hitting external APIs

Manual Testing (Postman):
- GET /api/prices/BTC/USD – should return value or 404
- GET /api/prices/history/BTC?range=7d – valid response
- GET /api/prices/history/BTC?range=99d – should fail
- Run scheduled fetch and inspect price retrieval manually

### 8. Edge Cases

- getPrice returns null when no data exists
- IllegalArgumentException thrown for unsupported time ranges
- Empty result list returned when no matching historical data exists
- Logs fallback behavior when CoinGecko is unavailable

### 9. Conclusion

The service was tested using a combination of unit, integration, and manual testing. Test cases cover core functionalities, edge behavior, and error handling. The approach aims to simulate a real-world microservice environment and applies practical quality assurance methods aligned with the project's scope and educational level.

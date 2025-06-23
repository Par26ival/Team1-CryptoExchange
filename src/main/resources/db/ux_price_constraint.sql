CREATE UNIQUE INDEX IF NOT EXISTS ux_price
       ON crypto_prices (crypto_code, fiat_code, "timestamp");
CREATE TABLE contracts (
  pid BIGSERIAL PRIMARY KEY,
  id VARCHAR(255) NOT NULL,
  correlation_id VARCHAR(255),
  status VARCHAR(255),
  created_at TIMESTAMP,
  expires_at TIMESTAMP,
  recipient_ark_address VARCHAR(255),
  deposit_eth_address VARCHAR(255),
  subscription_id VARCHAR(255),
  deposit_eth_private_key VARCHAR(255)
);

CREATE TABLE transfers (
  pid BIGSERIAL PRIMARY KEY,
  id VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  status VARCHAR(255),
  eth_transaction_id VARCHAR(255),
  eth_amount DECIMAL(8,5),
  eth_to_ark_rate DECIMAL(8,5),
  eth_flat_fee DECIMAL(8,5),
  eth_percent_fee DECIMAL(8,5),
  eth_total_fee DECIMAL(8,5),
  ark_transaction_id VARCHAR(255),
  ark_send_amount DECIMAL(8,5),
  contract_pid BIGINT NOT NULL
);

ALTER TABLE transfers ADD CONSTRAINT FOREIGN KEY (contract_pid) REFERENCES contracts (pid);

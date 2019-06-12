CREATE TABLE purchase_history (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  purchase_history_info JSON,
  ec_site VARCHAR(32),
  account_id VARCHAR(64),
  order_no VARCHAR(256),
  order_date TIMESTAMP,
  total_amount VARCHAR(16),
  delivery_status VARCHAR(256),
  update_at TIMESTAMP
);

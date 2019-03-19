ALTER TABLE purchase_history CHANGE COLUMN order_json purchase_history_info JSON;

ALTER TABLE purchase_history
  ADD COLUMN ec_site VARCHAR(32),
  ADD COLUMN user_id VARCHAR(64),
  ADD COLUMN order_no VARCHAR(256),
  ADD COLUMN order_date TIMESTAMP,
  ADD COLUMN total_amount VARCHAR(16),
  ADD COLUMN delivery_status VARCHAR(256),
  ADD COLUMN update_at TIMESTAMP;

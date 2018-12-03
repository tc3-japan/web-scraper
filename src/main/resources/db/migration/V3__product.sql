CREATE TABLE product (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  ec_site VARCHAR(32),
  product_code VARCHAR(64),
  product_name VARCHAR(256),
  unit_price VARCHAR(16),
  product_distributor VARCHAR(64),
  product_info JSON,
  fetch_info_status VARCHAR(32),
  update_at TIMESTAMP,
  UNIQUE KEY unique_product_code (product_code)
);

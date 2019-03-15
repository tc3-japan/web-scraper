CREATE TABLE purchase_product (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  product_code VARCHAR(64),
  product_name VARCHAR(256),
  product_quantity INT,
  unit_price VARCHAR(16),
  product_distributor VARCHAR(64),
  purchase_history_id INT,
  update_at TIMESTAMP,
  FOREIGN KEY (purchase_history_id) REFERENCES purchase_history(id)
);

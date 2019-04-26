CREATE TABLE product_group (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  model_no VARCHAR(256),
  grouping_method VARCHAR(64),
  confirmation_status VARCHAR(32),
  update_at TIMESTAMP
);

ALTER TABLE product
  ADD COLUMN model_no VARCHAR(256),
  ADD COLUMN group_status VARCHAR(32),
  ADD COLUMN product_group_id INT,
  ADD CONSTRAINT FOREIGN KEY (product_group_id) REFERENCES product_group(id)
  ;

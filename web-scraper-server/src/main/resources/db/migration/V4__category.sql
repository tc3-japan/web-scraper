CREATE TABLE product_category (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  ec_site VARCHAR(32),
  category_path VARCHAR(256),
  update_at TIMESTAMP
);

CREATE TABLE product_category_ranking (
  product_id INT,
  category_id INT,
  ranking INT,
  update_at TIMESTAMP,
  PRIMARY KEY (product_id, category_id),
  FOREIGN KEY (product_id) REFERENCES product(id),
  FOREIGN KEY (category_id) REFERENCES product_category(id)
);

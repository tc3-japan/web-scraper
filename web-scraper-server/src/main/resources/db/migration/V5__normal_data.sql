CREATE TABLE normal_data (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  ec_site VARCHAR(32),
  page VARCHAR(32),
  page_key VARCHAR(32),
  normal_data JSON,
  downloaded_at TIMESTAMP,
  UNIQUE KEY unique_index (ec_site, page, page_key)
);

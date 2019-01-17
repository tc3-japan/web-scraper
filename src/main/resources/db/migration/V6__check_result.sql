CREATE TABLE check_result (
  id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
  ec_site VARCHAR(32),
  page VARCHAR(32),
  page_key VARCHAR(32),
  total_check_status VARCHAR(16),
  check_result_detail JSON,
  checked_at TIMESTAMP,
  UNIQUE KEY unique_index (ec_site, page, page_key)
);

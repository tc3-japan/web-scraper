CREATE INDEX `idx_product_modelno` USING BTREE
  ON `product`(`model_no`);

CREATE INDEX `idx_product_jancode` USING BTREE
  ON `product`(`jan_code`);

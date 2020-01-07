CREATE INDEX `idx_product_group_modelno` USING BTREE
  ON `product_group`(`model_no`);

CREATE INDEX `idx_product_group_jancode` USING BTREE
  ON `product_group`(`jan_code`);

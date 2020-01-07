ALTER TABLE product
  ADD COLUMN jan_code VARCHAR(20)
  ;

ALTER TABLE product_group
  ADD COLUMN jan_code VARCHAR(20),
  ADD COLUMN product_name VARCHAR(2000)
  ;

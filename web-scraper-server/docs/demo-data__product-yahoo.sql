-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < demo-data__product-yahoo.sql

SET SQL_SAFE_UPDATES = 0;

delete from web_scraper.product_category_ranking;
delete from web_scraper.product_category;
delete from web_scraper.product;
delete from web_scraper.product_group;

insert into web_scraper.product (ec_site, product_code, product_name) values
 ('yahoo', 'giftnomori/822-002', 'The Golden Leaf -- from forest of gifts!')
,('yahoo', 'futureoffice/34-128',   'The Chair -- for your dream office!')
,('yahoo', 'cocoro-ystore/CB-EGG2', 'Dunno what this is')
;

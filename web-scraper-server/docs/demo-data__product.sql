-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < demo-data__product.sql

SET SQL_SAFE_UPDATES = 0;

delete from web_scraper.product_category_ranking;
delete from web_scraper.product_category;
delete from web_scraper.product;
delete from web_scraper.product_group;

insert into web_scraper.product (ec_site, product_code, product_name) values
 ('amazon', 'B07KH55LSD', 'シャープ SHARP ドラム式洗濯乾燥機(ハイブリッド乾燥)')
,('kojima', 'LCARBRCK',   '炊飯ジャー 「糖質カット炊飯器」(6合) LCARBRCK')
;

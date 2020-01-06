-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < demo-data__product-rakuten.sql

SET SQL_SAFE_UPDATES = 0;

delete from web_scraper.product_category_ranking;
delete from web_scraper.product_category;
delete from web_scraper.product;
delete from web_scraper.product_group;

insert into web_scraper.product (ec_site, product_code, product_name) values
 ('rakuten', 'auc-k-ebisuya/357-35271030', 'Men\'s wool sweater')
,('rakuten', 'rb/15603523', 'Razpie Magazine')
,('rakuten', 'marutsuelec/834053', 'Raspberry Pi Model 3B')
;

-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < <this sql>

SET SQL_SAFE_UPDATES = 0;
-- initialize data for batch=purchase_history
delete from web_scraper.purchase_product;
delete from web_scraper.purchase_history;

-- initialize data for batch=product
delete from web_scraper.product_category_ranking;
delete from web_scraper.product_category;
delete from web_scraper.product;
delete from web_scraper.product_group;

-- initialize data for all batches using TrafficWebClient
delete from web_scraper.request_event;
delete from web_scraper.tactic_event;

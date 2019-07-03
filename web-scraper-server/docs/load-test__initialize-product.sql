-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < <this sql>

SET SQL_SAFE_UPDATES = 0;
-- initialize data for batch=product
delete from web_scraper.product_category_ranking;
delete from web_scraper.product_category;
update web_scraper.product set fetch_info_status = null;

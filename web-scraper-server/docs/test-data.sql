-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < test-data.sql

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

-- initialize user data
delete from web_scraper.ec_site_account;
delete from web_scraper.user;

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values
 (1,'email@gmail.com','status','2020-10-10 12:00:00','2019-10-14 12:00:00');
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) values
 (1, 'amazon', 0, 1,'2019-03-08 12:00:00')
,(3, 'kojima', 0, 1,'2019-03-08 12:00:00')
,(5, 'yahoo',  0, 1,'2019-03-08 12:00:00');

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values
 (2,'email2@gmail.com','status','2020-10-10 12:00:00','2019-10-14 12:00:00');
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) values
 (2, 'amazon', 0, 2,'2019-03-08 12:00:00')
,(4, 'kojima', 0, 2,'2019-03-08 12:00:00')
,(6, 'yahoo',  0, 2,'2019-03-08 12:00:00');

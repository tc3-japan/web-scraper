SET SQL_SAFE_UPDATES = 0;
delete from web_scraper.purchase_product;
delete from web_scraper.purchase_history;

delete from web_scraper.ec_site_account;
delete from web_scraper.user;

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values (
  1,'email@gmail.com','status','2020-10-10 12:00:00','2019-10-14 12:00:00');
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) VALUES (
  '1', 'amazon', '0', '1','2019-03-08 12:00:00');

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values (
  2,'email2@gmail.com','status','2020-10-10 12:00:00','2019-10-14 12:00:00');
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) VALUES (
  '2', 'amazon', '0', '2','2019-03-08 12:00:00');


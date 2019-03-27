-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < encrypt-table.sql

alter table web_scraper.ec_site_account ENCRYPTION='Y';
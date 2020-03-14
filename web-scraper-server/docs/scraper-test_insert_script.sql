-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < docs/scraper-test_insert_script.sql

SET SQL_SAFE_UPDATES = 0;

delete from web_scraper.scraper;

insert into web_scraper.scraper (id,site,type,script) values
 (1,'amazon','purchase_history',load_file('/scripts/scraping/unified/amazon-purchase-history-list.groovy'))
,(2,'rakuten','purchase_history',load_file('/scripts/scraping/unified/rakuten-purchase-history-list.groovy'))
,(3,'yahoo','purchase_history',load_file('/scripts/scraping/unified/yahoo-purchase-history-list.groovy'));

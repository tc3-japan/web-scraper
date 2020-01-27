-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < test-data__product.sql

SET SQL_SAFE_UPDATES = 0;
delete from web_scraper.product;
delete from web_scraper.product_group;

insert into web_scraper.product (id, ec_site, product_code, product_name, unit_price, product_distributor, model_no, jan_code) values
 (1,  'amazon',  'B07KH55LSD', 'Drum type washer / dryer',      239114, 'SHARP',      'ES-W111-SR', '4974019970738')
,(2,  'kojima',  '3957065',    'Drum type washer',              250972, 'SHARP',      'ES-W111-SR', null)
,(3,  'yahoo',   'QWERTY001',  'Drum type washer(SHARP)',       252384, 'SHARP',      null,         '4974019970738')
,(4,  'rakuten', 'HJKL001',    'Drum type washer',              255000, 'SHARP',      'ES-W111-SR', null)
,(5,  'amazon',  'B06XJVGH11', 'Rice cooker IH Honsumigama',    43024,  'MITSUBISHI', 'NJ-SW069-B', null)
,(6,  'kojima',  '3792608',    'Rice cooker',                   51111,  'MITSUBISHI', 'NJ-SW069-B', null)
,(7,  'yahoo',   'QWERTY002',  'Rice cooker IH(Panasonic)',     51234,  'MITSUBISHI', null,         '4902901817519')
,(8,  'rakuten', 'HJKL002',    'Rice cooker',                   51234,  'MITSUBISHI', 'NJ-SW069-B', null)
,(9,  'amazon',  'B008PSWX9A', 'DELSEY Paris Luggage Helium',   255000, 'SHARP',      null,         null)
,(10, 'amazon',  'B008PSWX9B', 'DELSEY Paris Luggage Helium02', 255000, 'SHARP',      'ES-W111-SR', null)
,(11, 'amazon',  'B008PSWX9C', 'DELSEY Paris Luggage Helium03', 255000, 'SHARP',      'ES-W111-SB', null)
,(12, 'amazon',  'B07KH55LSF', 'Drum type washer / dryer',      239114, 'SHARP',      'ES-W111-SC', null)
;

/*
insert into web_scraper.product_group (id, model_no, grouping_method, confirmation_status) values
 (1, 'ES-W111-SR', 'Model-No-Grouping', 'unconfirmed')
,(2, 'NJ-SW069-B', 'Model-No-Grouping', 'unconfirmed')
;

update web_scraper.product set group_status = 'grouped', product_group_id = 1 where model_no = 'ES-W111-SR';
update web_scraper.product set group_status = 'grouped', product_group_id = 2 where model_no = 'NJ-SW069-B';
*/

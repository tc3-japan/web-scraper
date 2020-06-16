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

delete from web_scraper.configuration;

SET
@amazon_purchase_history_script = '
{
    "url": "https://www.amazon.co.jp/gp/your-account/order-history?opt=ab&digitalOrders=1&unifiedOrders=1&returnTo=&orderFilter=",
    "purchase_order": {
        "url_element": "",
        "parent": "html > body > div > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(5) > div.a-box-group.a-spacing-base.order",
        "order_number": {
            "element": "div:nth-of-type(1) > div > div > div > div:nth-of-type(2) > div:nth-of-type(1) > span:nth-of-type(2).a-color-secondary.value",
            "full_path": false,
            "attribute": "",
            "regex": ""
        },
        "order_date": {
            "element": "div:nth-of-type(1) > div > div > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div:nth-of-type(2) > span.a-color-secondary.value",
            "full_path": false,
            "attribute": "",
            "regex": ""
        },
        "total_amount": {
            "element": "div:nth-of-type(1) > div > div > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(2) > span.a-color-secondary.value",
            "full_path": false,
            "attribute": "",
            "regex": ""
        },
        "delivery_status": {
            "element": "div:nth-of-type(2) > div > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > span.js-shipment-info.aok-hidden",
            "full_path": false,
            "attribute": "data-yoshortstatuscode",
            "regex": ""
        },
        "purchase_product": {
            "url_element": "",
            "parent": "div > div > div > div > div:nth-of-type(1) > div > div > div.a-fixed-left-grid-inner",
            "product_code": {
                "element": "div:nth-of-type(2) > div:nth-of-type(1) > a.a-link-normal",
                "full_path": false,
                "attribute": "href",
                "regex": "/gp/product/([A-Z0-9]+)/"
            },
            "product_name": {
                "element": "div:nth-of-type(2) > div:nth-of-type(1) > a.a-link-normal",
                "full_path": false,
                "attribute": "",
                "regex": ""
            },
            "product_quantity": {
                "element": "div:nth-of-type(1) > div > span.item-view-qty",
                "full_path": false,
                "attribute": "",
                "regex": ""
            },
            "unit_price": {
                "element": "div:nth-of-type(2) > div:nth-of-type(4) > span.a-size-small.a-color-price",
                "full_path": false,
                "attribute": "",
                "regex": ""
            },
            "product_distributor": {
                "element": "div:nth-of-type(2) > div:nth-of-type(2) > span.a-size-small.a-color-secondary",
                "full_path": false,
                "attribute": "",
                "regex": ""
            }
       }
    },
    "next_url_element": ".a-last [href]"
}
',
@rakuten_purchase_history_script = '
{
  "url": "https://order.my.rakuten.co.jp/?l-id=top_normal_function04&fidomy=1",
  "purchase_order": {
    "parent": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div:nth-of-type(2) > div:nth-of-type(1) > div > div:nth-of-type(3) > div > table > tbody > tr",
    "order_number": {
      "element": "td:nth-of-type(1) > div > ul:nth-of-type(1) > li:nth-of-type(2) > span.idNum",
      "full_path": false,
      "attribute": "",
      "regex": ""
    },
    "order_date": {
      "element": "td:nth-of-type(1) > div > ul:nth-of-type(1) > li.purchaseDate",
      "full_path": false,
      "attribute": "",
      "regex": ""
    },
    "purchase_product": {
      "url_element": "td:nth-of-type(1) > div > ul:nth-of-type(1) > li:nth-of-type(4) > a.detail",
      "parent": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div:nth-of-type(2) > div > div:nth-of-type(3) > table:nth-of-type(1) > tbody > tr",
      "total_amount": {
        "element": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div:nth-of-type(2) > div > div:nth-of-type(3) > table:nth-of-type(2) > tbody > tr > td:nth-of-type(3) > table > tbody > tr > td.netTot",
        "full_path": true,
        "attribute": "",
        "regex": ""
      },
      "product_code": {
        "element": "td > table > tbody > tr > td:nth-of-type(2) > a.itemLink",
        "full_path": false,
        "attribute": "href",
        "regex": ".+item.rakuten.co.jp/([^/]+/[^/]+)/.*"
      },
      "product_name": {
        "element": "td > table > tbody > tr > td:nth-of-type(2) > a.itemLink",
        "full_path": false,
        "attribute": "",
        "regex": ""
      },
      "product_quantity": {
        "element": "td.widthQuantity.taRight",
        "full_path": false,
        "attribute": "",
        "regex": ""
      },
      "unit_price": {
        "element": "td.widthPrice.taRight",
        "full_path": false,
        "attribute": "",
        "regex": ""
      },
      "product_distributor": {
        "element": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div:nth-of-type(2) > div > div:nth-of-type(1) > table > tbody > tr:nth-of-type(1) > td:nth-of-type(2) > p > a",
        "full_path": true,
        "attribute": "",
        "regex": ""
      }
    }
  },
  "next_url_element": "#oDrCenterContents .clfx:nth-child(11) [data-ratid=\\\"ph_pc_pagi_next\\\"]"
}
',
@yahoo_purchase_history_script = '
{
    "url": "https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_my_MHD_order_history",
    "purchase_order": {
        "parent": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div:nth-of-type(2) > ul > li",
        "order_number": {
            "element": "div:nth-of-type(2) > ul > li > ul:nth-of-type(1) > li:nth-of-type(2) > dl > dd",
            "full_path": false,
            "attribute": "",
            "regex": ""
        },
        "order_date": {
            "element": "div:nth-of-type(1) > p:nth-of-type(1) > span",
            "full_path": false,
            "attribute": "",
            "regex": ""
        },
        "purchase_product": {
            "url_element": "div:nth-of-type(2) > ul > li > ul:nth-of-type(3) > li:nth-of-type(1) > a",
            "parent": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(3) > div:nth-of-type(2) > ul > li",
            "total_amount": {
                "element": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(2) > div > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(2) > ul > li.elSum > dl > dd",
                "full_path": true,
                "attribute": "",
                "regex": ""
            },
            "product_code": {
                "element": "div > ul > li:nth-of-type(2) > p > a",
                "full_path": false,
                "attribute": "href",
                "regex": "https://.*pagekey=(.*)"
            },
            "product_name": {
                "element": "div > dl > dd:nth-of-type(1) > a > span",
                "full_path": false,
                "attribute": "",
                "regex": ""
            },
            "product_quantity": {
                "element": "div > dl > dd > span:nth-of-type(2).elNum",
                "full_path": false,
                "attribute": "",
                "regex": ""
            },
            "unit_price": {
                "element": "div > dl > dd > span:nth-of-type(1).elPrice",
                "full_path": false,
                "attribute": "",
                "regex": ""
            },
            "product_distributor": {
                "element": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > ul:nth-of-type(1) > li:nth-of-type(1) > a",
                "full_path": true,
                "attribute": "",
                "regex": ""
            }
       }
    },
    "next_url_element": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div > ul > li:nth-of-type(2) > a"
}
';

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values
 (1,'email@gmail.com','status','2020-10-10 12:00:00','2019-10-14 12:00:00');
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) values
 (1, 'amazon',  0, 1,'2019-03-08 12:00:00')
,(3, 'kojima',  0, 1,'2019-03-08 12:00:00')
,(5, 'yahoo',   0, 1,'2019-03-08 12:00:00')
,(7, 'rakuten', 0, 1,'2019-03-08 12:00:00');

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values
 (2,'email2@gmail.com','status','2020-10-10 12:00:00','2019-10-14 12:00:00');
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) values
 (2, 'amazon',  0, 2,'2019-03-08 12:00:00')
,(4, 'kojima',  0, 2,'2019-03-08 12:00:00')
,(6, 'yahoo',   0, 2,'2019-03-08 12:00:00')
,(8, 'rakuten', 0, 2,'2019-03-08 12:00:00');

insert into web_scraper.configuration (id,site,type,config) values
 (1,'amazon','purchase_history',@amazon_purchase_history_script)
,(2,'rakuten','purchase_history',@rakuten_purchase_history_script)
,(3,'yahoo','purchase_history',@yahoo_purchase_history_script);

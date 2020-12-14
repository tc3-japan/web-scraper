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
  "url": "https://www.amazon.co.jp/gp/your-account/order-history?orderFilter=year-{year}",
  "purchase_order": {
    "url_element": "",
    "parent": "html > body > div > div > div:nth-of-type(1) > div > div.a-box-group.a-spacing-base.order",
    "order_number": {
      "element": "div:nth-of-type(1) > div > div > div > div:nth-of-type(2) > div:nth-of-type(1) > span:nth-of-type(2).a-color-secondary.value",
      "full_path": false,
      "attribute": "",
      "regex": "",
      "is_script": false,
      "script": ""
    },
    "order_date": {
      "element": "div:nth-of-type(1) > div > div > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div:nth-of-type(2) > span.a-color-secondary.value",
      "full_path": false,
      "attribute": "",
      "regex": "",
      "is_script": false,
      "script": ""
    },
    "total_amount": {
      "element": "div:nth-of-type(1) > div > div > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(2) > span.a-color-secondary.value",
      "full_path": false,
      "attribute": "",
      "regex": "",
       "is_script": false,
       "script": ""
     },
    "delivery_status": {
      "element": "div:nth-of-type(2) > div > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > span.js-shipment-info.aok-hidden",
      "full_path": false,
      "attribute": "data-yoshortstatuscode",
      "regex": "",
      "is_script": false,
      "script": ""
    },
    "purchase_product": {
      "url_element": "",
      "parent": "div > div > div > div > div:nth-of-type(1) > div > div > div.a-fixed-left-grid-inner",
      "product_code": {
        "element": "div:nth-of-type(2) > div:nth-of-type(1) > a.a-link-normal",
        "full_path": false,
        "attribute": "href",
        "regex": "/gp/product/([A-Z0-9]+)/",
        "is_script": false,
        "script": ""
      },
      "product_name": {
        "element": "div:nth-of-type(2) > div:nth-of-type(1) > a.a-link-normal",
        "full_path": false,
        "attribute": "",
        "regex": "",
         "is_script": false,
         "script": ""
       },
      "product_quantity": {
        "element": "div:nth-of-type(1) > div > span.item-view-qty",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "unit_price": {
        "element": "div:nth-of-type(2) > div > span.a-size-small.a-color-price",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "product_distributor": {
        "element": "div:nth-of-type(2) > div:nth-of-type(2) > span.a-size-small.a-color-secondary",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
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
    "parent": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div:nth-of-type(2) > div:nth-of-type(1) > div > div > div.oDrListItem.clfx > table > tbody > tr",
    "order_number": {
      "element": "td:nth-of-type(1) > div > ul:nth-of-type(1) > li:nth-of-type(2) > span.idNum",
      "full_path": false,
      "attribute": "",
      "regex": "",
      "is_script": false,
      "script": ""
    },
    "order_date": {
      "element": "td:nth-of-type(1) > div > ul:nth-of-type(1) > li.purchaseDate",
      "full_path": false,
      "attribute": "",
      "regex": "",
      "is_script": false,
      "script": ""
    },
    "purchase_product": {
      "url_element": "td:nth-of-type(1) > div > ul:nth-of-type(1) > li > a.detail",
      "parent": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div > div > div:nth-of-type(3) > table:nth-of-type(1) > tbody > tr",
      "total_amount": {
        "element": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div > div > div:nth-of-type(3) > table:nth-of-type(2) > tbody > tr > td:nth-of-type(3) > table > tbody > tr:nth-of-type(1) > td",
        "full_path": true,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "product_code": {
        "element": "td > table > tbody > tr > td:nth-of-type(2) > a.itemLink",
        "full_path": false,
        "attribute": "href",
        "regex": "rakuten.co.jp/([^/]+/[^/]+)/.*",
        "is_script": false,
        "script": ""
      },
      "product_name": {
        "element": "td > table > tbody > tr > td:nth-of-type(2) > a.itemLink",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "product_quantity": {
        "element": "td.widthQuantity.taRight",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "unit_price": {
        "element": "td.widthPrice.taRight",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "product_distributor": {
        "element": "html > body > div:nth-of-type(1) > div:nth-of-type(7) > div > div > div:nth-of-type(1) > table > tbody > tr:nth-of-type(1) > td:nth-of-type(2) > p > a",
        "full_path": true,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
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
      "regex": "",
      "is_script": false,
      "script": ""
    },
    "order_date": {
      "element": "div:nth-of-type(1) > p:nth-of-type(1) > span",
      "full_path": false,
      "attribute": "",
      "regex": "",
      "is_script": false,
      "script": ""
    },
    "purchase_product": {
      "url_element": "div:nth-of-type(2) > ul > li > ul:nth-of-type(3) > li:nth-of-type(1) > a",
      "parent": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(3) > div:nth-of-type(2) > ul > li",
      "total_amount": {
        "element": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(2) > div > div:nth-of-type(2) > div:nth-of-type(1) > div:nth-of-type(2) > ul > li.elSum > dl > dd",
        "full_path": true,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "product_code": {
        "element": "div > ul > li:nth-of-type(2) > p > a",
        "full_path": false,
        "attribute": "href",
        "regex": "https://.*pagekey=(.*)",
        "is_script": true,
        "script": "document.querySelector(''input[type=hidden][name=h_sid]'').value + ''/'' + document.querySelector(''#orddtl > div.elItem > ul > li:nth-child({productIndex}) > div > ul > li:nth-child(2) > p > a'').getAttribute(''href'').match(''https://.*pagekey=(.*)'')[1];"
      },
      "product_name": {
        "element": "div > dl > dd:nth-of-type(1) > a > span",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "product_quantity": {
        "element": "div > dl > dd > span:nth-of-type(2).elNum",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "unit_price": {
        "element": "div > dl > dd > span:nth-of-type(1).elPrice",
        "full_path": false,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      },
      "product_distributor": {
        "element": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div > div:nth-of-type(2) > ul:nth-of-type(1) > li:nth-of-type(1) > a",
        "full_path": true,
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      }
     }
  },
  "next_url_element": "html > body > div:nth-of-type(1) > div > div:nth-of-type(2) > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div > ul > li:nth-of-type(2) > a"
}
';

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values
 (1,'email@gmail.com','status', DATE_ADD(NOW(), INTERVAL 1 YEAR), NOW());
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) values
 (1, 'amazon',  0, 1, NOW())
,(3, 'yahoo',   0, 1, NOW())
,(5, 'rakuten', 0, 1, NOW());

insert into web_scraper.user (id,email_for_contact,total_ec_status,id_expire_at,update_at) values
 (2,'email2@gmail.com','status', DATE_ADD(NOW(), INTERVAL 1 YEAR), NOW());
insert into web_scraper.ec_site_account (id, ec_site, ec_use_flag, user_id, update_at) values
 (2, 'amazon',  0, 2, NOW())
,(4, 'yahoo',   0, 2, NOW())
,(6, 'rakuten', 0, 2, NOW());

insert into web_scraper.configuration (id,site,type,config) values
 (1,'amazon','purchase_history',@amazon_purchase_history_script)
,(2,'rakuten','purchase_history',@rakuten_purchase_history_script)
,(3,'yahoo','purchase_history',@yahoo_purchase_history_script);

SET
@amazon_product_detail_script = '{
  "url": "https://www.amazon.co.jp/gp/product/{code}/",
  "product_details":[
    [
      {
        "item":"unit_price",
        "selector":"#priceblock_ourprice",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"unit_price",
        "selector":"#priceblock_saleprice",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"unit_price",
        "selector":"#priceblock_dealprice",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"unit_price",
        "selector":"#MediaMatrix > div > div > ul > li.selected > span > span.a-button-selected > span > a > span > span.a-color-price",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"unit_price",
        "selector":"#olp-upd-new-freeshipping span.a-color-price",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"unit_price",
        "selector":"#olp-upd-new span.a-color-price",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      }
    ],
    [
      {
        "item":"product_name",
        "selector":"#productTitle",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      }
    ],
    [
      {
        "item":"model_no_label",
        "selector":"#prodDetails > div.wrapper.JPlocale table > tbody > tr.item-model-number > td.value",
        "attribute":"",
        "regex":"",
        "label_selector":"#prodDetails > div.wrapper.JPlocale table > tbody > tr.item-model-number > td.label",
        "label_value":"メーカー型番",
        "label_attribute":"",
        "label_regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"model_no_label",
        "selector":"#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(3)",
        "attribute":"",
        "regex":"",
        "label_selector":"#detail_bullets_id > table > tbody > tr > td > div > ul > li:nth-child(3) > b",
        "label_value":"メーカー型番",
        "label_attribute":"",
        "label_regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"model_no_label",
        "selector":"#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(2)",
        "attribute":"",
        "regex":"",
        "label_selector":"#detail_bullets_id > table > tbody > tr > td > div > ul > li:nth-child(2) > b",
        "label_value":"メーカー型番",
        "label_attribute":"",
        "label_regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"model_no_label",
        "selector":"#productDetailsTable ul:nth-of-type(1) li:nth-of-type(3)",
        "attribute":"",
        "regex":"商品モデル番号： (.*)",
        "label_selector":"#productDetailsTable li:nth-of-type(3) b",
        "label_value":"商品モデル番号：",
        "label_attribute":"",
        "label_regex":"",
        "is_script": false,
        "script":""
      }
    ],
    [
      {
        "item":"product_distributor",
        "selector":".zg_hrsr_item span:nth-of-type(1)",
        "attribute":"",
        "regex":"ブランド: (.*)",
        "is_script": false,
        "script":""
      },
      {
        "item":"product_distributor",
        "selector":"#bylineInfo",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      }
    ]
  ]
 }',
@rakuten_product_detail_script = '{
  "url": "https://item.rakuten.co.jp/{code}",
  "product_details": [
    [
      {
        "item": "unit_price",
        "selector": ".price2",
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      }
    ],
    [
      {
        "item": "product_name",
        "selector": ".item_name",
        "attribute": "",
        "regex": "",
        "is_script": false,
        "script": ""
      }
    ],
    [
      {
        "item": "jan_code",
        "selector": ".item_number",
        "attribute": "",
        "regex": "[0-9]{13}",
        "is_script": false,
        "script": ""
      },
      {
        "item": "model_no",
        "selector": ".item_number",
        "attribute": "",
        "regex": "^\\\\w+-\\\\w+$",
        "is_script": false,
        "script": ""
      }
    ],
    [
      {
        "item": "product_distributor",
        "selector":"form > input[type=hidden][name=sn]",
        "attribute":"value",
        "regex":"",
        "is_script": false,
        "script":""
      }
    ]
  ]
}',
@yahoo_product_detail_script = '{
  "url": "https://store.shopping.yahoo.co.jp/{code}",
  "product_details": [
    [
      {
        "item": "product_code",
        "selector": "head > link[rel=\\\"canonical\\\"]",
        "attribute": "href",
        "regex": "https:\\\/\\\/.*?\\\/(.*).html",
        "is_script": false,
        "script": ""
      }
    ],
    [
      {
        "item":"unit_price",
        "selector":".ItemPrice_price",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      },
      {
        "item":"unit_price",
        "selector":"p.elPrice:nth-child(2) > em",
        "attribute":"",
        "regex":"",
        "is_script": false,
        "script":""
      }
    ],
    [
      {
        "item":"jan_code",
        "selector":".mdItemInfoCode > p",
        "attribute":"",
        "regex":"[0-9]{13}",
        "is_script": false,
        "script":""
      },
      {
        "attribute":"",
        "item":"jan_code",
        "regex":"JANコード/ISBNコード：(.+)",
        "is_script": false,
        "script":"",
        "selector":".ItemDetails ul li:nth-of-type(2)"
      }
    ],
    [
      {
        "attribute":"",
        "item":"model_no",
        "regex":"商品コード：(.+)",
        "script":"(()=>{ var ifr = document.querySelector(''#itm_inf > div:nth-child(6) > div > iframe''); if (!ifr) return null; return ifr.contentDocument.body.querySelector(''#wrapper > table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(2) > td:nth-child(2) > font'').innerText; })();",
        "selector":"#wrapper > table > tbody > tr:nth-child(4) > td > table > tbody > tr:nth-child(2) > td:nth-child(2) > font",
        "is_script": false
      },
      {
        "attribute":"",
        "item":"model_no",
        "regex":"商品コード：(.+)",
        "script":"",
        "selector":".ItemDetails li"
      }
   ]
  ]
}',
@amazon_product_search_script = '{
  "url": "https://www.amazon.co.jp/s?k={word}",
  "group_selector": "html > body > div:nth-of-type(1) > div:nth-of-type(2) > div:nth-of-type(1) > div > div span:nth-of-type(3) > div:nth-of-type(2) > div.sg-col-4-of-24.sg-col-4-of-12.sg-col-4-of-36.s-result-item.s-asin.sg-col-4-of-28.sg-col-4-of-16.sg-col.sg-col-4-of-20.sg-col-4-of-32",
  "selector": "div > span > div > div div:nth-of-type(2) > h2 > a",
  "attribute": "href",
  "regex": "/dp/([A-Z0-9]+)/",
  "is_script": false,
  "script": "",
  "excluded_selector": "div > span > div > div div:nth-of-type(2) > div > span > span > span:nth-of-type(1) > span"
}',
@rakuten_product_search_script = '{
  "url": "https://search.rakuten.co.jp/search/mall/{word}",
  "group_selector": "html > body > div:nth-of-type(3) > div:nth-of-type(2) > div > div > div.dui-card.searchresultitem",
  "selector": "div:nth-of-type(2) > h2 > a",
  "attribute": "href",
  "regex": "item.rakuten.co.jp\\\/(.+?\\\/.+?)\\\/",
  "is_script": false,
  "script": "",
  "excluded_selector": "div:nth-of-type(2) > h2 > span.dui-tag.-pr"
}',
@yahoo_product_search_script = '{
  "url": "https://shopping.yahoo.co.jp/search?p={word}",
  "group_selector":"html > body > div:nth-of-type(1) > div > div > main > div:nth-of-type(6) > div:nth-of-type(1) > div:nth-of-type(5) > ul > li > div > ul > li.LoopList__item",
  "selector":"div > div:nth-of-type(2) > p > a._2EW-04-9Eayr",
  "attribute": "data-beacon",
  "regex": "targurl:store.shopping.yahoo.co.jp\\\/(.+?\\\/.+?).html",
  "is_script": false,
  "script": "",
  "excluded_selector": ""
}';

insert into web_scraper.configuration (id,site,type,config) values
 (4,'amazon','product',@amazon_product_detail_script)
,(5,'rakuten','product',@rakuten_product_detail_script)
,(6,'yahoo','product',@yahoo_product_detail_script)
,(7,'amazon','search',@amazon_product_search_script)
,(8,'rakuten','search',@rakuten_product_search_script)
,(9,'yahoo','search',@yahoo_product_search_script);

-- Usage: mysql --host 0.0.0.0 --port 3306 --user root --password web_scraper < docs/scraper-test_insert_script.sql

SET SQL_SAFE_UPDATES = 0;

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
                "regex": "\/gp\/product\/([A-Z0-9]+)\/"
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
import com.topcoder.common.model.ProductInfo;
print "\\n\\nScraping Rakuten Purchase History\\n\\n"
def htmlPath = "https://order.my.rakuten.co.jp/?l-id=top_normal_function04&fidomy=1"

setEnableJS(true)
setPage(htmlPath)
savePage("test", "rakuten-history")

processPurchaseHistory() {
	orderList = scrapeDomList "div.oDrListItem"
	print "orderList loop"
	//print "ORDER LIST" + orderList
	int index = 0;
	processOrders(orderList) { orderNode ->
		index += 1;
		//print "looping through orderNode" + orderNode
		debug "orderNode loop"
		scrapeOrderNumber(orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > div:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > span:nth-child(1)")
		scrapeOrderDate(orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > div:nth-child(1) > ul:nth-child(1) > li:nth-child(1)")

		//if (!isNew()) { return false; } //PROBLEM! Date changes per order not per item!

		//productList = scrapeDomList orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > div:nth-child(1) > div:nth-child(1)"
		productList = scrapeDomList orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > div"
		debug "productList: " + productList.size()

		openPage(orderNode, ".purchaseInfo a.detail") { orderDetailPage ->

			distributor = getText orderDetailPage, ".oDrSpecShopInfo .shopName > a"
			purchaseHistory.totalAmount = getText orderDetailPage, ".priceCalc .netTot"

			productList = scrapeDomList(orderDetailPage, ".oDrSpecPurchaseInfo tr")
			processProducts(productList) { productNode ->

				String prodInfo = getText productNode, ".prodInfo"
				if (!prodInfo?.trim())  { // skip if prodInfo is null or empty
					return
				}
				scrapeProductCodeFromAnchor productNode, ".prodInfo .prodName a", ".+item.rakuten.co.jp/([^/]+/[^/]+)/.*"
				scrapeProductName productNode, ".prodInfo .prodName a"
				scrapeUnitPrice productNode, ".widthPrice"
				scrapeProductQuantity productNode, ".widthQuantity"
				productInfo.distributor = distributor
			}
		}
	}
}
',
@yahoo_purchase_history_script = '
setEnableJS(true)
setPage("https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history")
savePage("test", "yahoo")

processPurchaseHistory() {
    // scrape order dom node list
    orderList = scrapeDomList(".elMain > ul > li")

    // loop each order
    processOrders(orderList) { orderNode ->
        // scrape order details
        //#ordhist > div.elMain > ul > li:nth-child(1) > div.elItem > ul > li > ul.elSummary > li.elOrder > dl > dd
        scrapeOrderNumber orderNode, "div.elItem > ul > li > ul.elSummary > li.elOrder > dl > dd"

        //#ordhist > div.elMain > ul > li:nth-child(1) > div.elOrderHeader > p.elDate > span
        scrapeOrderDate   orderNode, "div.elOrderHeader > p.elDate > span"

        //if (!isNew()) { return false; }

        //#ordhist > div.elMain > ul > li:nth-child(1) > div.elItem > ul > li > ul.elButton > li.elDetail > a
        openPage(orderNode, "div.elItem > ul > li > ul.elButton > li.elDetail > a") { orderDetailPage ->

            //#orddtl > div.elItem > ul > li
            productList = scrapeDomList(orderDetailPage, "#orddtl > div.elItem > ul > li")
            processProducts(productList) { productNode ->

                // get product code from the URL like https://store.shopping.yahoo.co.jp/bookfan/BK-481540657X.html
                //#orddtl > div.elItem > ul > li > div > ul > li > form > input[name=".autodone"]
                scrapeProductCodeFromInput productNode, "div > ul > li > form > input[name=\\".autodone\\"]", "https:\\\\/\\\\/.*?\\\\/(.*)\\\\.html"

                //#orddtl > div.elItem > ul > li > div > dl > dd.elName > a > span
                scrapeProductName          productNode, "div > dl > dd.elName > a > span"

                //#orddtl > div.elItem > ul > li > div > dl > dd.elInfo > span.elPrice
                scrapeUnitPrice            productNode, "div > dl > dd.elInfo > span.elPrice"

                //#orddtl > div.elItem > ul > li > div > dl > dd.elInfo > span.elNum
                scrapeProductQuantity      productNode, "div > dl > dd.elInfo > span.elNum"

                // CAUTION: At yahoo distributer is out of product list, it''s common for products.
                //#buystr > div.elItem > ul.elStore > li.elName > a
                scrapeProductDistributor   orderDetailPage, "#buystr > div.elItem > ul.elStore > li.elName > a"
            }
        }
    }
}
';

insert into web_scraper.configuration (id,site,type,config) values
 (1,'amazon','purchase_history',@amazon_purchase_history_script)
,(2,'rakuten','purchase_history',@rakuten_purchase_history_script)
,(3,'yahoo','purchase_history',@yahoo_purchase_history_script);

--  (1,'amazon','purchase_history',load_file('../scripts/scraping/unified/amazon-purchase-history-list.groovy'))
-- ,(2,'rakuten','purchase_history',load_file('../scripts/scraping/unified/rakuten-purchase-history-list.groovy'))
-- ,(3,'yahoo','purchase_history',load_file('../scripts/scraping/unified/yahoo-purchase-history-list.groovy'));

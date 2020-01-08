import com.topcoder.common.model.ProductInfo;
print "\n\nScraping Rakuten Purchase History\n\n"
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
		log "orderNode loop"
		scrapeOrderNumber(orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > div:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > span:nth-child(1)")
		scrapeOrderDate(orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > div:nth-child(1) > ul:nth-child(1) > li:nth-child(1)")

		distoributor = getText orderNode, ".purchaseShop .shopName"

		//if (!isNew()) { return false; } //PROBLEM! Date changes per order not per item!

		//productList = scrapeDomList orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > div:nth-child(1) > div:nth-child(1)"
		productList = scrapeDomList orderNode, "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(2) > div"
		log "productList: " + productList.size()

		//REWRITE BELOW TO PROPERLY ITERATE THROUGH PRODUCT DETAIL PAGES!
		//See yahoo script on develop & Feature/update-modules-groovy-unified

		processProducts(productList) { productNode ->
			setPage(htmlPath) //This doesn't work?
			//print "Looping through product node: " + productNode

			productName = getText productNode, ".itemPriceCnt > .itemName a"

			qty = getText productNode, ".itemPriceCnt > .itemNum"
			qty = qty.replaceAll("[^\\d.]", "");
			productQty = qty.isInteger() ? qty.toInteger() : null

			productPrice = getText productNode, ".itemPriceCnt > .itemPrice"
			productPrice = productPrice.replaceAll("[^\\d.]", "");

			click productNode, "ul:nth-child(2) > li:nth-child(1) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > ul:nth-child(1) > li:nth-child(1) > a:nth-child(1)"
			productCode = getText ".item_number"
			distributor = getNodeAttribute "input[name=\"shopname\"]", "value"

			ProductInfo info = new ProductInfo(productCode, productName, productPrice, productQty, distributor)

			log "**** addProduct order:${purchaseHistory.orderNumber}, code:$productCode, name:$productName, price:$productPrice, qty:$productQty, distributor:$distributor"
			addProduct(info)
		}
	}
}
/*

def getOrderNodes() {
	setPage "https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history"
	return;
}


def getItemNodes() {
	return;
}



def processItemNodes() {
	ProductInfo productInfo = new ProductInfo();
	return productInfo;
}
 */

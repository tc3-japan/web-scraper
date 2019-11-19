print "\n\nScraping Yahoo Purchase History\n\n"

def htmlPath = "https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history"

setEnableJS(true)
setPage(htmlPath)
savePage("test", "yahoo")

processPurchaseHistory() {
	orderList = scrapeDomList ".elMain > ul:nth-child(1)"
	print "orderList loop"
	//print "ORDER LIST" + orderList

	processOrders(orderList) { orderNode ->
		//print "looping through orderNode" + orderNode
		print "orderNode loop"
		scrapeOrderNumber(orderNode, "div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > dl:nth-child(1) > dd:nth-child(2)")
		scrapeOrderDate(orderNode, "div:nth-child(1) > p:nth-child(1) > span:nth-child(1)")
	
		if (!isNew()) { return false; }
	
		//productList = scrapeDomList ""
		//processProducts(productList) { productNode ->
			//print "Looping through product node: " + productNode
			//scrapeProductQuantity productNode, ""
			//scrapeUnitPrice productNode, ""
		//}
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

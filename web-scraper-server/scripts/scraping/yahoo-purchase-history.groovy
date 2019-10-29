print "\n\nScraping Yahoo Purchase History\n\n"

def htmlPath = "https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history"
/*
setEnableJS(true)
setPage(htmlPath)
setOrderNumber(orderNode, "div:nth-child(2) > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(1) > li:nth-child(2) > dl:nth-child(1) > dd:nth-child(2)")
setOrderDate(orderNode, "div:nth-child(1) > p:nth-child(1) > span:nth-child(1)")

savePage("test", "yahoo")
*/

/**
 *returns orderNodes
 */
def getOrderNodes() {
	setPage "https://odhistory.shopping.yahoo.co.jp/cgi-bin/history-list?sc_i=shp_pc_top_MHD_order_history"
	return;
}

/**
 *returns itemNodes
 */
def getItemNodes() {
	return;
}

/**
 *orderNode, itemNode
 *returns productInfo
 */
def processItemNodes() {
	ProductInfo productInfo = new ProductInfo();
	return productInfo;
}


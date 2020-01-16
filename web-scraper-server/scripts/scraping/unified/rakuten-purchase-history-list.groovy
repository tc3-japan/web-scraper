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

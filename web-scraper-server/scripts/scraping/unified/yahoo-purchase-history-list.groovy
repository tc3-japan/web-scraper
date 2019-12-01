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
                scrapeProductCodeFromInput productNode, "div > ul > li > form > input[name=\".autodone\"]", "https:\\/\\/.*?\\/(.*)\\.html"

                //#orddtl > div.elItem > ul > li > div > dl > dd.elName > a > span
                scrapeProductName          productNode, "div > dl > dd.elName > a > span"

                //#orddtl > div.elItem > ul > li > div > dl > dd.elInfo > span.elPrice
                scrapeUnitPrice            productNode, "div > dl > dd.elInfo > span.elPrice"

                //#orddtl > div.elItem > ul > li > div > dl > dd.elInfo > span.elNum
                scrapeProductQuantity      productNode, "div > dl > dd.elInfo > span.elNum"

                // CAUTION: At yahoo distributer is out of product list, it's common for products.
                //#buystr > div.elItem > ul.elStore > li.elName > a
                scrapeProductDistributor   orderDetailPage, "#buystr > div.elItem > ul.elStore > li.elName > a"
            }
        }
    }
}

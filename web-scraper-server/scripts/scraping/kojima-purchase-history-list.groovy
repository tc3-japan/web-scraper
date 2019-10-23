// set scraping page
setPage "https://www.kojima.net/ec/member/CMmOrderHistory.jsp"

// start to process purchase history
processPurchaseHistory() {

    // scrape order dom node list
    orderList = scrapeDomList ".member-orderhistorydetails > tbody" // ordersBox
    // loop each order
    processOrders(orderList) { orderNode ->
        // scrape order details
        scrapeOrderNumberWithRegex orderNode, ".itemnumber", "([\\d]{13})" // orderNumber, pattern
        scrapeOrderDateDefault     orderNode, ".itemnumber"                // orderDate, default pattern and format
        scrapeTotalAmount          orderNode, ".totalamountmoney"          // totalAmount
        //scrapeDeliveryStatus       orderNode, ""                           // *NO* deliveryStatus

        if (!isNew()) { return false; }

        // scrape product dom node list
        productList = scrapeDomList ".member-orderhistorydetails > tbody > tr" // productsBox
        // loop each product
        processProducts(productList) { productNode ->
            // scrape product details
            //scrapeProductCode           productNode, ""           // *NO* productCode
            scrapeProductName           productNode, ".itemname"  // productName
            scrapeProductQuantity       productNode, ".itemnum"   // productQuantity
            scrapeUnitPrice             productNode, ".itemprice" // unitPrice
            //scrapeProductDistributor    productNode, ""           // *NO* productDistributor
        }
    }
}


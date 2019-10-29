// set scraping page
setPage "https://www.amazon.co.jp/gp/your-account/order-history?opt=ab&digitalOrders=1&unifiedOrders=1&returnTo=&orderFilter="

// start to process purchase history
processPurchaseHistory() {

    // scrape order dom node list
    orderList = scrapeDomList "#ordersContainer > div.order" // ordersBox
    // loop each order
    processOrders(orderList) { orderNode ->
        // scrape order details
        scrapeOrderNumber    orderNode, "div.order-info > div > div > div > div:nth-of-type(2) > div:nth-of-type(1) > span:nth-of-type(2)"             // orderNumber
        scrapeOrderDate      orderNode, "div.order-info > div > div > div > div:nth-of-type(1) > div > div:nth-of-type(1) > div:nth-of-type(2) > span" // orderDate
        scrapeTotalAmount    orderNode, "div.order-info > div > div > div > div:nth-of-type(1) > div > div:nth-of-type(2) > div:nth-of-type(2) > span" // totalAmount
        scrapeDeliveryStatus orderNode, "div.shipment > div > div:nth-of-type(1) > div:nth-of-type(1) > div:nth-of-type(2) > span:nth-of-type(1)"      // deliveryStatus

        if (!isNew()) { return false; }

        // scrape product dom node list
        productList = scrapeDomList "div.shipment > div > div > div > div:nth-of-type(1) > div > div.a-fixed-left-grid" // productsBox
        // loop each product
        processProducts(productList) { productNode ->
            // scrape product details
            scrapeProductCodeFromAnchor productNode, "div > div:nth-of-type(2) > div:nth-of-type(1) > a", "\\/gp\\/product\\/([A-Z0-9]+)\\/" // productAnchor, pattern
            scrapeProductNameFromAnchor productNode, "div > div:nth-of-type(2) > div:nth-of-type(1) > a"                                     // productAnchor
            scrapeProductQuantity       productNode, "span.item-view-qty"                                                                    // productQuantity
            scrapeUnitPrice             productNode, "span.a-color-price"                                                                    // unitPrice
            scrapeProductDistributor    productNode, "span.a-color-secondary"                                                                // productDistributor
        }
    }
}

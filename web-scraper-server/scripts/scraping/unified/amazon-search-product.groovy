// Script Main
log("--------------script begin--------------")

searchProducts("https://www.amazon.co.jp/s?k=")
saveListPage("product-list")

eachProducts { index ->
    searchResultSelector = "#search div[data-index='" + index + "']"
    adProductClass       = "AdHolder"
    productCodeAttribute = "data-asin"
    scrapeProductCodeFromSearchResultByProductAttrName(searchResultSelector, productCodeAttribute, adProductClass)
}

log("--------------script end--------------")


// Script Main
debug("--------------script begin--------------")

searchProducts("https://www.amazon.co.jp/s?k=")
//saveListPage("product-list")

eachProducts { index ->
    searchResultSelector = "#search div[data-index='" + index + "']"
    productCodeAttribute = "data-asin"
    adProductClass       = "AdHolder"
    scrapeProductCodeFromSearchResultByProductAttrName(searchResultSelector, productCodeAttribute, adProductClass)
}

debug("--------------script end--------------")


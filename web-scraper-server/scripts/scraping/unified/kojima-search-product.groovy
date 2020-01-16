// Script Main
debug("--------------script begin--------------")

searchProductsUsingForm(
        "https://www.kojima.net/ec/top/CSfTop.jsp",
        "search_form", "q", "#btnSearch"
)
saveListPage("product-list")

eachProducts { index ->
    searchResultSelector = "#category_item_list > li:nth-child(" + (index+1) + ") > a"
    productCodeRegex     = ".*GOODS_STK_NO=(.*)"
    scrapeProductCodeFromSearchResultByProductUrl(searchResultSelector, productCodeRegex)
}

debug("--------------script end--------------")


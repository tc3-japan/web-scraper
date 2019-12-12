// Script Main
log("--------------script begin--------------")

searchProducts("https://shopping.yahoo.co.jp/search?p=")
saveListPage("product-list")

eachProducts { index ->
    //#searchResults1 > div:nth-child(3) > ul > li:nth-child(1)
    //data-ylk="rsec:v_imsrg;pos:1;pg:1;slk:img;tar:store.shopping.yahoo.co.jp;tar_uri:/ai-en/pg-btaux.html"
    //data-beacon="sec:rsltlst;pos:1;pg:1;slk:title;catid:49495;targurl:store.shopping.yahoo.co.jp/mt-import/0638126675552.html"
    searchResultSelector = "#searchResults1 > div:nth-child(3) > ul > li:nth-child(" + (index+1) + ") > div > div > a"
    productCodeAttribute = "data-beacon"
    adProductClass       = null
    productCodeRegex     = ".*targurl:.*?\\/(.*?\\/.*?).html"
    scrapeProductCodeFromSearchResultByProductAttrName(searchResultSelector, productCodeAttribute, adProductClass, productCodeRegex)
}

log("--------------script end--------------")


log "Kojima TEST"
log productId
log productId
setPage "https://www.kojima.net/ec/top/CSfTop.jsp"
setEnableJS true
type productId, "#q"
savePage "test", "yahoo"
click "#btnSearch"
click "#category_item_list > li:nth-child(1) > a:nth-child(1) > img:nth-child(2)"
savePage "test-after-click", "yahoo"
setName "h1.htxt02"
setDistributor "span"
setPrice "td.price > span"
//setQuantity ".cart_box > div:nth-child(1) > form:nth-child(1) > input:nth-child(2)"
setModelNo "#item_detail > div > div.item_detail_box > table > tbody > tr:nth-child(6) > td"

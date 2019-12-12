def htmlPath = "https://store.shopping.yahoo.co.jp/"
setEnableJS(false)
setPage(htmlPath + productCode)
savePage("yahoo-" + productCode.replace("/", "_"))

log(" >>> Requesting Page >>> " + htmlPath + productCode)

// #abuserpt > p:nth-child(3)
// head > link[rel='canonical']
scrapeCodeFromAttr("head > link[rel='canonical']", "href", "https:\\/\\/.*?\\/(.*)\\.html");

// .mdItemInfoTitle > h2:nth-child(1)
scrapeName(".mdItemInfoTitle > h2")

// dt.elStore > a:nth-child(1)
scrapeDistributor("dt.elStore > a")

// .elNum
// .ItemPrice_price
// p.elPrice:nth-child(2) > em:nth-child(1)
scrapePrices([".elNum", ".ItemPrice_price", "p.elPrice:nth-child(2) > em"])

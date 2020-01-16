def htmlPath = "https://store.shopping.yahoo.co.jp/"
setEnableJS(false)
setPage(htmlPath + productCode)
savePage("yahoo-" + productCode.replace("/", "_"))

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

def jan = scrapeText(".mdItemInfoCode > p")
log "JAN : $jan"
productInfo?.janCode = normalize removeLabel(jan)

// "JANコード/ISBNコード:4988617257443" -> "4988617257443"
def removeLabel (text) {
  text ? text.split("：|:")[-1].trim() : text
}

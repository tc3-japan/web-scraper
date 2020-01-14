def htmlPath = "https://item.rakuten.co.jp/"
setEnableJS(true);
setPage(htmlPath + productCode)
savePage("test-"+productCode)
log(" >>> Requesting Page >>> " + htmlPath + productCode)

// [Name]
scrapeName ".item_name"
//books - scrapeName "#productTitle"

// [Code]
// No need to update Product code obtained in purchase history crawling.
//scrapeCode(".item_number");
//books - scrapeCode("li.productInfo:nth-child(7) > span:nth-child(2)");

// [Distributor]
scrapeDistributor "input[name=\"shopname\"]"

// [Price]
scrapePrice(".price2");
//books - scrapePrice(".price");

// [JAN Code]
code = scrapeText ".item_number"
if (code?.trim() ==~ /[0-9]{13}/) {
  productInfo.janCode = code
  log "**** JAN: ${code}"
}

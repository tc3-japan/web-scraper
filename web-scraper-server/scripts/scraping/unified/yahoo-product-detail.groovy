def htmlPath = "https://store.shopping.yahoo.co.jp/"
setEnableJS(false);
setPage(htmlPath + productCode)
savePage("test-"+productCode, "yahoo")
log(" >>> Requesting Page >>> " + htmlPath + productCode)

scrapeCode("#abuserpt > p:nth-child(3)");
scrapeName("div.elTitle > h2:nth-child(1)");
scrapeDistributor("dt.elStore > a:nth-child(1)");
scrapePrice(".elNum");

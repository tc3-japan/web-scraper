def htmlPath = "https://store.shopping.yahoo.co.jp/"
setEnableJS(false);
setPage(htmlPath + productCode)
savePage("test-"+productCode, "yahoo")
log(" >>> Requesting Page >>> " + htmlPath + productCode)

setCode("#abuserpt > p:nth-child(3)");
setName("div.elTitle > h2:nth-child(1)");
setDistributor("dt.elStore > a:nth-child(1)");
setPrice(".elNum");

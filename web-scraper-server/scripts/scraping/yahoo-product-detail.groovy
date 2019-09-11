def htmlPath = "https://store.shopping.yahoo.co.jp/"
setPage(htmlPath + productCode)
log(" >>> Requesting Page >>> " + htmlPath)

setCode("#abuserpt > p:nth-child(3)");
setName("div.elTitle > h2:nth-child(1)");
setDistributor("dt.elStore > a:nth-child(1)");
setPrice(".elNum");

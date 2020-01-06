def htmlPath = "https://item.rakuten.co.jp/"
setEnableJS(true);
setPage(htmlPath + productCode)
savePage("test-"+productCode, "rakuten")
log(" >>> Requesting Page >>> " + htmlPath + productCode)

//Name
//general
scrapeName(".item_name > b:nth-child(1)");
//books
scrapeName("#productTitle > h1:nth-child(1)"); //get #text inside of this? Not sure how to select.

//Code
//general
scrapeCode(".item_number");
//books
//scrapeCode("li.productInfo:nth-child(7) > span:nth-child(2)");
//scrapeCode("li.productInfo:nth-child(6) > span:nth-child(2)");
//Need to search for productInfo child where span:nth-child(1) has the value of "ISBNコード"

//Distributor
/* Get from URL */

//Price
//general
scrapePrice(".price2");
//books
scrapePrice(".price");


//Product Info
//general
scrapeProductInfo(".item_desc") //MAKE THIS FUNCTION
//books
scrapeProductInfo(".saleDesc") //MAKE THIS FUNCTION

//Unique Product Code
/* get from URL */

//TODO: write functions to get the Code, Distributor, UID
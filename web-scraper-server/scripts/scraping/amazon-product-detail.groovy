// Configurations
def productUrlBase =
        "https://www.amazon.co.jp/gp/product/"
        //"https://www.amazon.com/gp/product/"
def prices = [
        "#priceblock_ourprice",
        "#MediaMatrix > div > div > ul > li.selected > span > span.a-button-selected > span > a > span > span.a-color-price"
]
def name =
        "#productTitle"
def salesRank =
        "#SalesRank"
def productInfoTable =
        "#productDetails_detailBullets_sections1"
def modelNoLabels = [
        "div.wrapper.JPlocale > div.column.col1 > div > div.content.pdClearfix > div > div > table > tbody > tr:nth-child(8) > td.label",
        "#prodDetails > div.wrapper.JPlocale > div.column.col1 > div > div.content.pdClearfix > div > div > table > tbody > tr:nth-child(2) > td.label",
        "#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(3) > b",
        "#detail_bullets_id > table > tbody > tr > td > div > ul > li:nth-child(2) > b",
        "#detail_bullets_id > table > tbody > tr > td > div > ul > li:nth-child(3) > b",
        "#prodDetails > div.wrapper.JPlocale > div.column.col1 > div > div.content.pdClearfix > div > div > table > tbody > tr:nth-child(2) > td.label",
        "#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(2) > b"
]
def modelNoLabelValues = [
        "製品型番",
        "製品型番",
        "製品型番",
        "メーカー型番",
        "メーカー型番",
        "モデル",
        "製造元リファレンス"
]
def modelNoValues = [
        "div.wrapper.JPlocale > div.column.col1 > div > div.content.pdClearfix > div > div > table > tbody > tr:nth-child(8) > td.value",
        "#prodDetails > div.wrapper.JPlocale > div.column.col1 > div > div.content.pdClearfix > div > div > table > tbody > tr:nth-child(2) > td.value",
        "#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(3)",
        "#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(2)",
        "#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(3)",
        "#prodDetails > div.wrapper.JPlocale > div.column.col1 > div > div.content.pdClearfix > div > div > table > tbody > tr:nth-child(2) > td.value",
        "#detail_bullets_id > table > tbody > tr > td > div.content > ul > li:nth-child(2)"
]


// Script Main
info("--------------script begin--------------")

def productUrl = productUrlBase + productCode;
info("product url: " + productUrl)
navigatePage(productUrl)

scrapePrice(prices)
scrapeName(name)
scrapeModelNo(modelNoLabels, modelNoLabelValues, modelNoValues)

categoryInfoList = scrapeCategoryInfoListBySalesRank(salesRank) { props ->
    props.put("1st_line_regex", "(.*?) - (.*?)位")
    props.put("rest_ranks_selector", "ul > li > span:nth-of-type(1)")
    props.put("rest_paths_selector", "ul > li > span:nth-of-type(2)")
}
if (categoryInfoList.size() <= 0) {
    categoryInfoList = scrapeCategoryInfoListByProductInfoTable(productInfoTable, { props ->
        props.put("lines_selector", "tbody > tr")
        props.put("ranks_selector", "td > span > span")
    }, { tr_node ->
        //Boolean.valueOf(getTextContent(tr_node.querySelector("th")).contains("Rank"))
        getTextContent(tr_node.querySelector("th")).contains("Rank")
    })
}
scrapeCategoryRanking(categoryInfoList)

save()
info("--------------script end--------------")


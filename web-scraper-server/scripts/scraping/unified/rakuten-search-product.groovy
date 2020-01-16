// Script Main
log("--------------script begin--------------")

searchProducts("https://search.rakuten.co.jp/search/mall/")
saveListPage("product-list")

eachProducts { index ->

    idx = index + 1

    pr = scrapeText ".searchresults .searchresultitem:nth-child(${idx}) .title .-pr"
    //log "**** PR: $pr"
    if (pr != null) {
        return null
    }
    link = getNodeAttribute ".searchresults .searchresultitem:nth-child(${idx}) .title a", "href"
    //log "**** PROD LINK: $link"
	pcode = extractProductCode(link)
    //log "**** PROD CODE: $pcode"
    return pcode
}

log("--------------script end--------------")

/**
 * -> https://item.rakuten.co.jp/akindo/hdr-cx680-ti/
 * --> akindo/hdr-cx680-ti
 */
def extractProductCode(productLink) {
    //m = productLink =~ /.+_url=([^&]+).*/
    //if (m?.size() == 0) {
    //    return null
    //}
    //actualLink = URLDecoder.decode(m[0][1])
    m = productLink =~ /.+item.rakuten.co.jp\/([^\/]+\/[^\/]+)\/.*/
    return m?.size() > 0 ? m[0][1] : null
}

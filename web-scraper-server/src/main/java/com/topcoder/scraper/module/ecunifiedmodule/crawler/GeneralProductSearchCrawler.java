package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.topcoder.common.model.scraper.ProductSearchConfig;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.HtmlUtils;
import com.topcoder.scraper.lib.navpage.NavigableProductListPage;
import com.topcoder.scraper.service.WebpageService;

public class GeneralProductSearchCrawler extends AbstractGeneralCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductSearchCrawler.class);

  public GeneralProductSearchCrawler(String site, String type, WebpageService webpageService, ConfigurationRepository configurationRepository) {
    super(site, type, webpageService, configurationRepository);
  }

  public GeneralProductSearchCrawlerResult searchProduct(TrafficWebClient webClient, String searchWord) throws IOException {
    LOGGER.debug("[searchProduct] in");
    LOGGER.debug(String.format("param search word=[%s]", searchWord));
    NavigableProductListPage listPage  = new NavigableProductListPage(webClient);
    ProductSearchConfig productSearchConfig = new ObjectMapper().readValue(jsonConfigText, ProductSearchConfig.class);
    String url = productSearchConfig.getUrl().replace("{word}", searchWord);
    String groupSelector = productSearchConfig.getGroupSelector();
    String selector = productSearchConfig.getSelector();
    String excludedSelector = productSearchConfig.getExcludedSelector();
    String regex = productSearchConfig.getRegex();
    String attribute = productSearchConfig.getAttribute();
    String script = productSearchConfig.getScript();
    if (StringUtils.isEmpty(url)) {
      LOGGER.debug("[url] not found");
      return null;
    }
    if (StringUtils.isEmpty(groupSelector)) {
      LOGGER.debug("[groupSelector] not found");
      return null;
    }
    if (StringUtils.isEmpty(selector)) {
      LOGGER.debug("[selector] not found");
      return null;
    }
    if (StringUtils.isEmpty(attribute)) {
      LOGGER.debug("[attribute] not found");
      return null;
    }
    listPage.setPage(url);
    LOGGER.debug(String.format("[url]=[%s]", url));
    String productCode = "";
    // javascript
    if (!StringUtils.isEmpty(script)) {
      productCode = listPage.executeJavaScript(listPage.getPage(), script);
    } else {
      if (!StringUtils.isEmpty(excludedSelector)) {
        DomNode node = listPage.getPage().querySelector(groupSelector);
        if (Objects.nonNull(node)) {
          node = node.querySelector(excludedSelector);
          if (Objects.isNull(node)) {
            return null;
          }
        }
      }
      productCode = listPage.getNodeAttribute(groupSelector + " > " + selector, attribute);
      if (StringUtils.isEmpty(productCode)) {
        DomNode node = listPage.getPage().querySelector(groupSelector);
        if (Objects.nonNull(node)) {
          node = node.querySelector(selector);
          productCode = node.getAttributes().getNamedItem(attribute).getNodeValue();
        }
      }
      LOGGER.debug(String.format("attribute[%s]=[%s]", attribute, productCode));
      if (Objects.nonNull(productCode) && Objects.nonNull(regex)) {
        productCode = HtmlUtils.extract1(productCode, Pattern.compile(regex));
        LOGGER.debug(String.format("regex=[%s]", regex));
      }
    }
    // for rakuten
    if (!StringUtils.isEmpty(productCode)) {
      productCode = productCode.replace("%2F", "/");
    }
    LOGGER.debug(String.format("return product_code=[%s]", productCode));
    String savedPath = listPage.savePage(site, "product-list", searchWord, listPage, webpageService);
    return new GeneralProductSearchCrawlerResult(productCode, savedPath);
  }

}

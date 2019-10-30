package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.common.util.HtmlUtils;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.service.WebpageService;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public abstract class AbstractProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProductDetailCrawler.class);

  protected final Binding               scriptBinding;
  protected final CompilerConfiguration scriptConfig;
  protected GroovyShell                 scriptShell;
  protected String                      scriptText = "";

  protected String savedPath;

  @Getter@Setter protected WebpageService   webpageService;
  @Getter@Setter protected TrafficWebClient webClient;
  @Getter@Setter protected String           siteName;

  @Getter@Setter protected String           productCode;
  @Getter@Setter protected ProductInfo      productInfo;
  @Getter@Setter protected NavigableProductDetailPage detailPage;

  public AbstractProductDetailCrawler(String siteName, WebpageService webpageService) {
    this.siteName = siteName;
    this.webpageService = webpageService;

    String scriptPath = this.getScriptPath();
    this.scriptText   = this.getScriptText(scriptPath);

    Properties configProps = new Properties();
    configProps.setProperty("groovy.script.base", this.getScriptSupportClassName());
    this.scriptConfig  = new CompilerConfiguration(configProps);
    this.scriptBinding = new Binding();
  }

  private String getScriptPath() {
    LOGGER.info("[getScriptPath] in");

    String scriptPath = System.getenv(Consts.SCRAPING_SCRIPT_PATH);
    if (StringUtils.isEmpty(scriptPath)) {
      scriptPath = System.getProperty("user.dir") + "/scripts/scraping";
    }
    scriptPath  += "/isolated/" + this.siteName + "-product-detail.groovy";

    LOGGER.info("scriptPath: " + scriptPath);
    return scriptPath;
  }

  private String getScriptText(String scriptPath) {
    LOGGER.info("[getScriptText] in");

    try {
      return FileUtils.readFileToString(new File(scriptPath), "utf-8");
    } catch (IOException e) {
      LOGGER.info("Could not read script file: " + scriptPath);
      return null;
    }
  }

  protected abstract String getScriptSupportClassName();

  // helpers
  private String executeScript() {
    LOGGER.info("[executeScript] in");
    this.scriptShell = new GroovyShell(this.scriptBinding, this.scriptConfig);
    Script script = scriptShell.parse(this.scriptText);
    script.invokeMethod("setCrawler", this);
    String resStr = (String)script.run();
    return resStr;
  }

  // methods
  public AbstractProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode) throws IOException {
    LOGGER.info("[fetchProductInfo] in");

    this.webClient  = webClient;
    this.detailPage = new NavigableProductDetailPage(this.webClient);

    this.productCode = productCode;
    this.productInfo = new ProductInfo();
    this.productInfo.setCode(productCode);
    this.detailPage.setProductInfo(this.productInfo);

    // binding variables for scraping script
    this.scriptBinding.setProperty("productCode", this.productCode);

    this.executeScript();

    return new AbstractProductDetailCrawlerResult(this.productInfo, this.savedPath);
  }

  void save() {
    this.savedPath = this.webpageService.save("product", this.siteName, this.detailPage.getPage().getWebResponse().getContentAsString());
  }

  // Wrapper
  String getTextContent(HtmlElement element) {
    return HtmlUtils.getTextContent(element);
  }

  public abstract void scrapeCategoryRanking(List<String> categoryInfoList);

  public abstract List<String> scrapeCategoryInfoListBySalesRank(String salesRankSelector, Closure<?> setProps);

  public abstract List<String> scrapeCategoryInfoListByProductInfoTable(String productInfoTableSelector, Closure<?> setProps, Closure<Boolean> rankLineTest);
}

package com.topcoder.scraper.module.ecunifiedmodule.crawler;

import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.lib.navpage.NavigableProductDetailPage;
import com.topcoder.scraper.service.WebpageService;
import groovy.lang.Binding;
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
import java.util.Properties;

public class GeneralProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GeneralProductDetailCrawler.class);

  private final Binding               scriptBinding;
  private final CompilerConfiguration scriptConfig;
  private GroovyShell                 scriptShell;
  private String                      scriptText = "";

  private String savedPath;

  @Getter@Setter private WebpageService   webpageService;
  @Getter@Setter private TrafficWebClient webClient;
  @Getter@Setter private String           siteName;

  @Getter@Setter private String           productCode;
  @Getter@Setter private ProductInfo      productInfo;
  @Getter@Setter private NavigableProductDetailPage detailPage;

  public GeneralProductDetailCrawler(String siteName, WebpageService webpageService) {
    LOGGER.info("[constructor] in");

    this.siteName = siteName;
    this.webpageService = webpageService;

    String scriptPath = this.getScriptPath();
    this.scriptText   = this.getScriptText(scriptPath);

    Properties configProps = new Properties();
    configProps.setProperty("groovy.script.base", this.getScriptSupportClassName());
    this.scriptConfig  = new org.codehaus.groovy.control.CompilerConfiguration(configProps);
    this.scriptBinding = new Binding();
  }

  private String getScriptPath() {
    LOGGER.info("[getScriptPath] in");

    String scriptPath = System.getenv(Consts.SCRAPING_SCRIPT_PATH);
    if (StringUtils.isEmpty(scriptPath)) {
      scriptPath = System.getProperty("user.dir") + "/scripts/scraping";
    }
    scriptPath  += "/unified/" + this.siteName + "-product-detail.groovy";

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

  private String getScriptSupportClassName() {
    return GeneralProductDetailCrawlerScriptSupport.class.getName();
  }

  private String executeScript() {
    LOGGER.info("[executeScript] in");
    this.scriptShell = new GroovyShell(this.scriptBinding, this.scriptConfig);
    Script script = scriptShell.parse(this.scriptText);
    script.invokeMethod("setCrawler", this);
    String resStr = (String)script.run();
    return resStr;
  }

  public GeneralProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode) throws IOException {
    LOGGER.info("[fetchProductInfo] in");

    this.webClient  = webClient;
    this.detailPage = new NavigableProductDetailPage(this.webClient);

    this.webClient   = webClient;
    this.productCode = productCode;
    this.productInfo = new ProductInfo();
    this.productInfo.setCode(productCode);

    // binding variables for scraping script
    this.scriptBinding.setProperty("productCode", this.productCode);

    this.executeScript();

    return new GeneralProductDetailCrawlerResult(this.productInfo, this.savedPath);
  }
}

package com.topcoder.scraper.module.ecisolatedmodule.crawler;

import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.Consts;
import com.topcoder.scraper.service.WebpageService;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public abstract class AbstractProductDetailCrawler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProductDetailCrawler.class);

  // variables
  private final Binding               scriptBinding;
  private final CompilerConfiguration scriptConfig;
  private GroovyShell                 scriptShell;
  private String                      scriptText = "";

  // TODO : to public and add accessors
  public final String         siteName;
  public final WebpageService webpageService;

  public TrafficWebClient webClient;
  public String           savedPath;
  public String           productCode;
  public ProductInfo      productInfo;

  // constructor
  public AbstractProductDetailCrawler(String siteName, WebpageService webpageService) {

    //super(siteName, webpageService);
    this.siteName = siteName;
    this.webpageService = webpageService;
    // TODO: doc
    AbstractProductDetailCrawlerScriptSupport.setCrawler(this);

    // TODO: doc
    String scriptPath = this.getScriptPath();
    this.scriptText   = this.getScriptText(scriptPath);

    // TODO: doc
    Properties configProps = new Properties();
    configProps.setProperty("groovy.script.base", this.getScriptSupportClassName());
    this.scriptConfig  = new CompilerConfiguration(configProps);
    this.scriptBinding = new Binding();
  }

  protected abstract String getScriptSupportClassName();

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

  // helpers
  private String executeScript() {
    LOGGER.info("[executeScript] in");
    this.scriptShell = new GroovyShell(this.scriptBinding, this.scriptConfig);
    Script script = scriptShell.parse(this.scriptText);
    String resStr = (String)script.run();
    return resStr;
  }

  // methods
  public AbstractProductDetailCrawlerResult fetchProductInfo(TrafficWebClient webClient, String productCode) throws IOException {
    LOGGER.info("[fetchProductInfo] in");

    AbstractProductDetailCrawlerScriptSupport.setProductId(productCode);

    this.webClient   = webClient;
    this.productCode = productCode;
    this.productInfo = new ProductInfo();
    this.productInfo.setCode(productCode);

    // binding variables for scraping script
    // >> What is this? Why pass productInfo this way?
    this.scriptBinding.setProperty("productCode", this.productCode);
    this.scriptBinding.setProperty("productInfo", this.productInfo);

    this.executeScript();

    return new AbstractProductDetailCrawlerResult(this.productInfo, this.savedPath);
  }
}

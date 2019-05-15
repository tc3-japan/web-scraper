package com.topcoder.common.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/**
 * Monitor target definition property
 */
@Component

@PropertySources({
        @PropertySource(factory = YamlPropertySourceFactory.class, value = {"classpath:monitor-target-definition.yaml"}),
        @PropertySource(factory = YamlPropertySourceFactory.class, value = {"file:${monitorTargetFile}"}, ignoreResourceNotFound = true)
})
@ConfigurationProperties
public class MonitorTargetDefinitionProperty {

  private List<MonitorTargetCheckSite> checkSites;
  
  public List<MonitorTargetCheckSite> getCheckSites() {
    return checkSites;
  }

  public void setCheckSites(List<MonitorTargetCheckSite> checkSites) {
    this.checkSites = checkSites;
  }

  public static class MonitorTargetCheckSite {

    private String ecSite;
    private List<MonitorTargetCheckPage> checkPages;

    public void setEcSite(String ecSite) {
      this.ecSite = ecSite;
    }

    public void setCheckPages(List<MonitorTargetCheckPage> checkPages){
      this.checkPages = checkPages;
    }

    public String getEcSite() {
      return ecSite;
    }

    public List<MonitorTargetCheckPage> getCheckPages() {
      return checkPages;
    }
  }
  
  /**
   * Represents `check_pages` in monitor-target-definition.yaml
   */
  public static class MonitorTargetCheckPage {
    private String pageName;
    private List<String> checkTargetKeys;

    public void setPageName(String pageName) {
      this.pageName = pageName;
    }

    public void setCheckTargetKeys(List<String> checkTargetKeys) {
      this.checkTargetKeys = checkTargetKeys;
    }

    public String getPageName() {
      return pageName;
    }

    public List<String> getCheckTargetKeys() {
      return checkTargetKeys;
    }
  }
}

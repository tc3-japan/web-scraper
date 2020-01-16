package com.topcoder.scraper.command.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.repository.ProductRepository;
import com.topcoder.scraper.group.JanCodeProductGroupBuilder;
import com.topcoder.scraper.group.ModelNumberProductGroupBuilder;
import com.topcoder.scraper.group.ProductNameProductGroupBuilder;
import com.topcoder.scraper.service.ECSiteService;

@Component
public class GroupECProductsCommand {

  private static Logger logger = LoggerFactory.getLogger(GroupECProductsCommand.class);

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ModelNumberProductGroupBuilder modelNoProductGroupBuilder;

  @Autowired
  private JanCodeProductGroupBuilder janCodeProductGroupBuilder;

  @Autowired
  private ProductNameProductGroupBuilder productNameProductGroupBuilder;

  @Autowired
  private ECSiteService ecSiteService;

  public static final String OPT_PRODUCTID = "pid";

  public static final String OPT_GROUPINGMETHOD = "method";

  @Transactional
  public void run(ApplicationArguments arguments) {
    logger.info("Start GroupECProductsCommand.");

    boolean pidSpecified = false;
    if (arguments.containsOption(OPT_PRODUCTID)) {
      logger.info("--pid " + ArrayUtils.toString(arguments.getOptionValues(OPT_PRODUCTID)));
      pidSpecified = true;
    }

    String method = null;
    if (arguments.containsOption(OPT_GROUPINGMETHOD)) {
      logger.info("--method " + ArrayUtils.toString(arguments.getOptionValues(OPT_GROUPINGMETHOD)));
      method = arguments.getOptionValues(OPT_GROUPINGMETHOD).get(0);
    }

    List<ProductDAO> ungroupedProducts = pidSpecified ? getSpecifiedProdicts(arguments) : findUngroupedProducts();

    if (ungroupedProducts == null || ungroupedProducts.size() == 0) {
      logger.info("Found no product which has not been in any group.");
      return;
    }

    Set<String> ecSites = ecSiteService.getAllECSites();

    final String groupingMethod = method;
    ungroupedProducts.forEach(product -> {

      Map<Integer, ProductDAO> groupedProducts = new HashMap<>();

      if (groupingMethod == null || "model-no".equalsIgnoreCase(groupingMethod)) {
        putAll(groupedProducts, modelNoProductGroupBuilder.createProductGroup(product, ecSites)); // model-no
      }

      if (groupingMethod == null || "jan-code".equalsIgnoreCase(groupingMethod)) {
        Set<String> targetSites = getIncompleteSites(getECSites(groupedProducts), ecSites);
        if (targetSites.size() > 0) {
          putAll(groupedProducts, this.janCodeProductGroupBuilder.createProductGroup(product, targetSites)); // jan-code
        }
      }

      if (groupingMethod == null || "product-name".equalsIgnoreCase(groupingMethod)) {
        Set<String> targetSites = getIncompleteSites(getECSites(groupedProducts), ecSites);
        if (targetSites.size() > 0) {
          putAll(groupedProducts, this.productNameProductGroupBuilder.createProductGroup(product, targetSites)); // product-name
        }
      }
    });
  }

  private void putAll(Map<Integer, ProductDAO> map, List<ProductDAO> products) {
    if (map == null || products == null) {
      return;
    }
    for (ProductDAO p : products) {
      if (p.getId() < 1) {
        continue;
      }
      map.put(p.getId(), p);
    }
  }

  private Set<String> getIncompleteSites(Set<String> completeSites, Set<String> allSites) {
    Set<String> incomplete = new HashSet<>();
    allSites.forEach(s -> {
      if (!completeSites.contains(s)) {
        incomplete.add(s);
      }
    });
    return incomplete;
  }

  public List<ProductDAO> getSpecifiedProdicts(ApplicationArguments arguments) {
    if (arguments == null || !arguments.containsOption(OPT_PRODUCTID)) {
      return new ArrayList<ProductDAO>(0);
    }
    List<Integer> pids = new LinkedList<>();
    arguments.getOptionValues(OPT_PRODUCTID).forEach(pid -> {
      for (String p : pid.split(",")) {
        logger.info("Specified product: " + p);
        pids.add(new Integer(p));
      }
    });
    if (pids.size() == 0) {
      return new ArrayList<ProductDAO>(0);
    }
    return this.productRepository.findByIdIn(pids);
  }

  public Set<String> getECSites(Map<Integer, ProductDAO> productsMap) {
    Set<String> ecSites = new HashSet<String>();
    if (productsMap == null || productsMap.size() == 0) {
      return ecSites;
    }
    productsMap.values().forEach(p -> {
      if (!StringUtils.isBlank(p.getEcSite())) {
        ecSites.add(p.getEcSite());
      }
    });
    return ecSites;
  }

  public List<ProductDAO> findUngroupedProducts() {
    return this.productRepository.findByGroupStatusIsNullOrProductGroupIdIsNull();
  }
}

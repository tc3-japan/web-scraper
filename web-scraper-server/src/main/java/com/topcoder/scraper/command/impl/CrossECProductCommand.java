package com.topcoder.scraper.command.impl;

import com.gargoylesoftware.htmlunit.Page;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.dao.ProductGroupDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.repository.ProductGroupRepository;
import com.topcoder.common.repository.ProductRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.amazon.crawler.AmazonProductDetailCrawler;
import com.topcoder.scraper.module.amazon.crawler.AmazonProductDetailCrawlerResult;
import com.topcoder.scraper.service.ProductService;
import com.topcoder.scraper.service.WebpageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This will group product information of all products where group_status is
 * null or uninitialized, to the appropriate product_group table.
 */
@Component
@Transactional
public class CrossECProductCommand {

  @Autowired
  ProductRepository productRepository;

  @Autowired
  ProductGroupRepository productGroupRepository;

  //////////
  @Autowired
  AmazonProperty property; // No idea where this comes from
  @Autowired
  WebpageService webpageService;
  @Autowired
  ProductService productService;
  // private final ProductRepository productRepository;

  // AmazonChangeDetectionInitModule initMod;
  //////////

  private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

  public void run(ApplicationArguments arguments) {
    List<ProductDAO> productDAOList = productRepository.findByGroupStatusIsNullOrProductGroupIdIsNull();
    logger.info("found " + productDAOList.size() + " un grouped product");
    Map<String, List<ProductDAO>> productDAOMap = new HashMap<>();
    productDAOList.forEach(productDAO -> {
      if (productDAO.getModelNo() != null) {
        List<ProductDAO> productDAOS = productDAOMap.get(productDAO.getModelNo());
        if (productDAOS == null) {
          productDAOS = new LinkedList<>();
        }
        productDAOS.add(productDAO);
        productDAOMap.put(productDAO.getModelNo(), productDAOS);
      }
    });

    productDAOMap.forEach((key, productDAOS) -> {
      logger.info("start group " + key + " with item count = " + productDAOS.size());
      if (productDAOS.size() <= 1) {
        logger.info("skip group " + key + " , because of item count < 2");
        // Find this product on other EC Site
        // Get:
        /*
         * <div class="s-result-list sg-row"> <div data-asin="OIEJAFOIJ" data-index="0"
         * << This
         */
        // Scrape and save to DB *don't need to worry about access time; available
        // immediately
        // Check that katabans ===
        // Just import and call AmazonProductDetailCrawlerResult
        // fetchProductInfo(TrafficWebClient webClient, String productCode, boolean
        // saveHtml)
        // for the ASIN codes of the product discovered
        // *See AmazonProductDetail.java #50ish for example of calling
        // Modify (*add) fetchProductDetail -> fetchProductDetailIfSameModelNo in
        // AmazonProductDetail.java
        // Append to list... *that we're looping over

        TrafficWebClient twc = new TrafficWebClient(0, false);
        String siteName = "amazon";
        String kataban = "74301900"; //tea candle lantern
        Page page = null;
        try {
          page = twc.getPage("https://www.amazon.co.jp/s?k=" + kataban);
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        System.out.println();
        System.out.println(">>>>>>> " + page);
        System.out.println();

        // ********* For now, assume ASIN (hardcode) and continue. Matsudasan will
        // provide example code to scrape

        // Search for related objects on Amazon
        String asin = "B07H4FQ7P5";
        AmazonProductDetailCrawler crawler = new AmazonProductDetailCrawler(siteName, property, webpageService);
        try {
          AmazonProductDetailCrawlerResult crawlerResult = crawler.fetchProductInfo(twc, asin, false);
          System.out.println("");
          System.out.println("");
          System.out.println(asin);
          // System.out.println(initMod);
          System.out.println(">>> crawlerResult: " + crawlerResult);
          System.out.println("");
          System.out.println("");
          ProductInfo productInfo = crawlerResult.getProductInfo();

          // Save ProductDAO, if product is not in DB
          ProductDAO existingProductDao = productRepository.findByProductCode(productInfo.getCode());
          if (existingProductDao == null) {
            ProductDAO productDao = new ProductDAO(siteName, productInfo);
            productRepository.save(productDao);
            int productId = productDao.getId();
            for (int i = 0; i < productInfo.getCategoryList().size(); i++) {
              String category = productInfo.getCategoryList().get(i);
              Integer rank = productInfo.getRankingList().get(i);
              productService.addCategoryRanking(productId, category, rank);
            }
          } else {
            existingProductDao.setUpdateAt(new Date());
            productRepository.save(existingProductDao);
            int productId = existingProductDao.getId();
            for (int i = 0; i < productInfo.getCategoryList().size(); i++) {
              String category = productInfo.getCategoryList().get(i);
              Integer rank = productInfo.getRankingList().get(i);
              productService.addCategoryRanking(productId, category, rank);
            }
          }

        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
        return;
      }
      ProductGroupDAO groupDAO = productGroupRepository.getByModelNo(key);
      if (groupDAO == null) {
        groupDAO = new ProductGroupDAO();
        groupDAO.setModelNo(key);
        groupDAO.setConfirmationStatus(ProductGroupDAO.ConfirmationStatus.unconfirmed);
        groupDAO.setGroupingMethod(ProductGroupDAO.GroupingMethod.same_no);
      }
      groupDAO.setUpdateAt(Date.from(Instant.now()));
      productGroupRepository.save(groupDAO);

      ProductGroupDAO finalGroupDAO = groupDAO;
      productDAOS.forEach(product -> {
        product.setGroupStatus(ProductDAO.GroupStatus.grouped);
        product.setProductGroupId(finalGroupDAO.getId());
        productRepository.save(product);
      });
    });
  }
}

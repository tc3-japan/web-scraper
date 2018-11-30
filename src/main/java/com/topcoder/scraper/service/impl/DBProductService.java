package com.topcoder.scraper.service.impl;

import com.topcoder.scraper.dao.CategoryDAO;
import com.topcoder.scraper.dao.ProductDAO;
import com.topcoder.scraper.model.ProductInfo;
import com.topcoder.scraper.repository.CategoryRepository;
import com.topcoder.scraper.repository.ProductRepository;
import com.topcoder.scraper.service.ProductService;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBProductService implements ProductService {

  private CategoryRepository categoryRepository;
  private ProductRepository productRepository;

  @Autowired
  public DBProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
    this.categoryRepository = categoryRepository;
    this.productRepository = productRepository;
  }

  @Override
  @Transactional
  public void addCategoryRanking(int productId, String categoryPath, int rank) {
    ProductDAO product = productRepository.findOne(productId);

    CategoryDAO category = this.categoryRepository.findByEcSiteAndCategoryPath(product.getEcSite(), categoryPath);
    if (category == null) {
      category = new CategoryDAO(product.getEcSite(), categoryPath, new Date());
    }
    this.categoryRepository.save(category);
    product.addCategory(category, rank);
  }

  @Override
  @Transactional
  public void update(int productId, String price, String fetchInfoStatus) {
    ProductDAO product = productRepository.findOne(productId);
    product.setUnitPrice(price);
    product.setFetchInfoStatus(fetchInfoStatus);
    product.setUpdateAt(new Date());

    // update product info
    ProductInfo info = product.getProductInfo();
    info.setPrice(price);
    product.setProductInfo(info);

  }


  @Override
  public List<ProductDAO> getAllFetchInfoStatusIsNull() {
    return this.productRepository.findByFetchInfoStatusIsNull();
  }
}

package com.topcoder.scraper.service.impl;

import com.topcoder.common.dao.CategoryDAO;
import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.repository.CategoryRepository;
import com.topcoder.common.repository.ProductRepository;
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
  public void updateProduct(int productId, ProductInfo productInfo) {
    ProductDAO product = productRepository.findOne(productId);
    ProductInfo info = product.getProductInfo();

    if (productInfo.getCode() != null) {
      product.setProductCode(productInfo.getCode());
      info.setCode(productInfo.getCode());
    }
    if (productInfo.getName() != null) {
      product.setProductName(productInfo.getName());
      info.setName(productInfo.getName());
    }
    if (productInfo.getPrice() != null) {
      product.setUnitPrice(productInfo.getPrice());
      info.setPrice(productInfo.getPrice());
    }
    if (productInfo.getDistributor() != null) {
      product.setProductDistributor(productInfo.getDistributor());
      info.setDistributor(productInfo.getDistributor());
    }
    
    if (productInfo.getModelNo() != null) {
        product.setModelNo(productInfo.getModelNo());
        info.setModelNo(productInfo.getModelNo());
    }

    product.setProductInfo(info);
    product.setUpdateAt(new Date());
  }

  @Override
  @Transactional
  public void updateFetchInfoStatus(int productId, String fetchInfoStatus) {
    ProductDAO product = productRepository.findOne(productId);
    product.setFetchInfoStatus(fetchInfoStatus);
    product.setUpdateAt(new Date());
  }


  @Override
  public List<ProductDAO> getAllFetchInfoStatusIsNull(String ecSite) {
    return this.productRepository.findByFetchInfoStatusAndECSite(ecSite);
  }
  
  @Override
  @Transactional
  public void saveProduct(String site, ProductInfo productInfo) {
	ProductDAO existingProductDao = null;
	if (productInfo.getCode() != null) {
	  existingProductDao = productRepository.findByProductCode(productInfo.getCode());
	} else {
	  existingProductDao = productRepository.findByECSiteAndProductName(site, productInfo.getName());
	}
	if (existingProductDao == null) {
	  ProductDAO productDao = new ProductDAO(site, productInfo);
	  productRepository.save(productDao);
	} else {
	  existingProductDao.setUpdateAt(new Date());
	  productRepository.save(existingProductDao);
	}
  }
}

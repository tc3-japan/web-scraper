package com.topcoder.api.controller;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.ProductGroupService;
import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.dao.ProductGroupDAO;
import com.topcoder.common.model.GroupRequest;
import com.topcoder.common.model.SearchProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * rest api for product and product group
 */
@RestController
@RequestMapping("/")
public class ProductGroupController {

  @Autowired
  ProductGroupService productGroupService;


  /**
   * search products
   *
   * @param request the search request
   * @return the page result
   */
  @PostMapping("/products/search")
  public Page<ProductDAO> searchProducts(@RequestBody SearchProductRequest request) {
    return productGroupService.searchProducts(request);
  }

  /**
   * create product group
   *
   * @param request the create request body
   */
  @PostMapping("/product_groups")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void createOrUpdateGroup(@RequestBody GroupRequest request) throws ApiException {
    productGroupService.createOrUpdateGroup(request);
  }

  /**
   * get product groups
   */
  @GetMapping("/product_groups")
  public List<ProductGroupDAO> getAllGroups() {
    return productGroupService.getAllGroups();
  }

  /**
   * delete group
   *
   * @param groupId the group id
   */
  @DeleteMapping("/product_groups/{groupId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGroup(@PathVariable Integer groupId) throws ApiException {
    productGroupService.deleteGroup(groupId);
  }

  /**
   * update group
   *
   * @param groupId  the group id
   * @param groupDAO the the group item
   */
  @PutMapping("/product_groups/{groupId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateGroup(@PathVariable Integer groupId, @RequestBody ProductGroupDAO groupDAO) throws ApiException {
    productGroupService.updateGroup(groupId, groupDAO);
  }

}

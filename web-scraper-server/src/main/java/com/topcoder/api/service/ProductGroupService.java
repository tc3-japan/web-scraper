package com.topcoder.api.service;

import com.topcoder.api.exception.BadRequestException;
import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.common.dao.ProductDAO;
import com.topcoder.common.dao.ProductGroupDAO;
import com.topcoder.common.model.GroupRequest;
import com.topcoder.common.model.SearchProductRequest;
import com.topcoder.common.repository.ProductGroupRepository;
import com.topcoder.common.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


/**
 * product group service
 */
@Service
public class ProductGroupService {

  /**
   * the product repository
   */
  @Autowired
  ProductRepository productRepository;

  /**
   * the productGroup Repository
   */
  @Autowired
  ProductGroupRepository productGroupRepository;


  /**
   * get group by id
   *
   * @param groupId the group id
   * @return the group
   * @throws EntityNotFoundException if not found
   */
  public ProductGroupDAO getProductGroup(Integer groupId) throws EntityNotFoundException {
    ProductGroupDAO productGroupDAO = productGroupRepository.findOne(groupId);
    if (productGroupDAO == null) {
      throw new EntityNotFoundException("Cannot found group where id = " + groupId);
    }
    return productGroupDAO;
  }


  /**
   * search products by filter
   *
   * @param request the filter
   * @return the page content
   */
  public Page<ProductDAO> searchProducts(SearchProductRequest request) {


    Iterable<ProductGroupDAO> groupDAOS;
    List<Integer> groupIdList = new LinkedList<>();
    if (request.getConfirmationStatus() != null) {
      groupDAOS = productGroupRepository.findByConfirmationStatus(request.getConfirmationStatus());
    } else {
      groupDAOS = productGroupRepository.findAll();
    }
    groupDAOS.forEach(groupDAO -> groupIdList.add(groupDAO.getId()));
    Pageable pageable = new PageRequest(request.getPageNo(), request.getPageSize(),
        Sort.Direction.ASC, "productGroupId");
    if (request.getSearchKeyword() != null && !request.getSearchKeyword().equals("")) {
      return productRepository.findProducts(groupIdList, request.getSearchKeyword(), pageable);
    }
    return productRepository.findByProductGroupIdIsNullOrProductGroupIdIn(groupIdList, pageable);
  }


  /**
   * create or update group
   * first find group by name, if existing, merge into this group, and update it
   * otherwise, create new group
   *
   * @param request the create request entity
   */
  public void createOrUpdateGroup(GroupRequest request) {

    ProductGroupDAO productGroupDAO = productGroupRepository.getByModelNo(request.getModelNo());
    if (productGroupDAO == null) {
      productGroupDAO = new ProductGroupDAO();
      productGroupDAO.setModelNo(request.getModelNo());
    }
    productGroupDAO.setGroupingMethod(ProductGroupDAO.GroupingMethod.manual);
    productGroupDAO.setUpdateAt(Date.from(Instant.now()));
    productGroupRepository.save(productGroupDAO); //save it

    ProductGroupDAO finalProductGroupDAO = productGroupDAO;
    if (request.getGroupIds() != null && request.getGroupIds().size() > 0) {
      request.getGroupIds().forEach(groupId -> productRepository.findAllByProductGroupId(groupId).forEach(productDAO -> {
        productDAO.setProductGroupId(finalProductGroupDAO.getId());
        productDAO.setGroupStatus(ProductDAO.GroupStatus.grouped);
        productRepository.save(productDAO);
      }));
    }

    if (request.getProductIds() != null && request.getProductIds().size() > 0) {
      request.getProductIds().forEach(productId -> {
        ProductDAO productDAO = productRepository.findById(productId);
        if (productDAO != null) {
          productDAO.setProductGroupId(finalProductGroupDAO.getId());
          productDAO.setGroupStatus(ProductDAO.GroupStatus.grouped);
          productRepository.save(productDAO);
        }
      });
    }
  }


  /**
   * delete group and set null for products
   *
   * @param groupId the group id
   * @throws EntityNotFoundException if group not exist
   */
  public void deleteGroup(Integer groupId) throws EntityNotFoundException {
    getProductGroup(groupId);
    List<ProductDAO> productDAOS = productRepository.findAllByProductGroupId(groupId);
    productDAOS.forEach(productDAO -> {
      productDAO.setGroupStatus(null);
      productDAO.setProductGroupId(null);
      productRepository.save(productDAO);
    });
    productGroupRepository.delete(groupId);
  }

  /**
   * update group
   *
   * @param groupId  the group id
   * @param groupDAO the group entity
   * @throws EntityNotFoundException if group not found
   * @throws BadRequestException     if params error
   */
  public void updateGroup(Integer groupId, ProductGroupDAO groupDAO) throws EntityNotFoundException, BadRequestException {
    ProductGroupDAO dbEntity = getProductGroup(groupId);
    if (groupDAO.getConfirmationStatus() != null) {
      dbEntity.setConfirmationStatus(groupDAO.getConfirmationStatus());
    }
    if (groupDAO.getModelNo() != null && !dbEntity.getModelNo().equals(groupDAO.getModelNo())) {
      if (productGroupRepository.getByModelNo(groupDAO.getModelNo()) != null) {
        throw new BadRequestException("Model No " + groupDAO.getModelNo() + " already exist");
      }
      dbEntity.setModelNo(groupDAO.getModelNo());
    }
    dbEntity.setUpdateAt(Date.from(Instant.now()));
    productGroupRepository.save(dbEntity);
  }

  /**
   * get all groups
   *
   * @return the groups
   */
  public List<ProductGroupDAO> getAllGroups() {
    List<ProductGroupDAO> productGroupDAOS = new LinkedList<>();
    productGroupRepository.findAll().forEach(productGroupDAOS::add);
    return productGroupDAOS;
  }

}

package com.topcoder.api.controller;

import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.api.exception.ApiException;
import com.topcoder.api.service.ECSiteAccountService;
import com.topcoder.common.dao.ECSiteAccountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * rest api controller
 */
@RestController
@RequestMapping("/users")
public class ECSiteController {


  /**
   * the ec site service
   */
  @Autowired
  ECSiteAccountService ecSiteAccountService;


  /**
   * get all sites by user id
   *
   * @param userId the user id
   * @throws ApiException if any error happened
   */
  @GetMapping("/{userId}/ec_sites")
  public List<ECSiteAccountDAO> getAllEcSites(@PathVariable("userId") String userId) throws ApiException {
    return ecSiteAccountService.getAll(userId);
  }

  /**
   * get ec site by site id
   *
   * @param userId the user id
   * @param id     the site id
   * @throws ApiException if any error happened
   */
  @GetMapping("/{userId}/ec_sites/{siteId}")
  public ECSiteAccountDAO getECSite(@PathVariable("userId") String userId, @PathVariable("siteId") Integer id) throws ApiException {
    return ecSiteAccountService.getECSite(userId, id);
  }

  /**
   * update ec site by id
   *
   * @param userId the user id
   * @param id     the site id
   * @param entity the ec site request entity
   * @throws ApiException if any error happened
   */
  @PutMapping("/{user_id}/ec_sites/{siteId}")
  public ECSiteAccountDAO updateECSite(@PathVariable("user_id") String userId,
                                       @PathVariable("siteId") Integer id,
                                       @RequestBody ECSiteAccountDAO entity) throws ApiException {
    return ecSiteAccountService.updateECSite(userId, id, entity);
  }

  /**
   * get login init things
   *
   * @param userId the user id
   * @param siteId the site id
   * @param uuid   the task id
   * @throws ApiException if any error happened
   */
  @GetMapping("/{userId}/login_init")
  public LoginResponse loginInit(@PathVariable("userId") String userId,
                                 @RequestParam("siteId") Integer siteId,
                                 @RequestParam("uuid") String uuid)
    throws ApiException {
    return ecSiteAccountService.loginInit(userId, siteId, uuid);
  }

  /**
   * do login
   *
   * @param userId  the user id
   * @param request the login request
   * @throws ApiException if any error happened
   */
  @PostMapping("/{userId}/login")
  public LoginResponse login(@PathVariable("userId") String userId, @RequestBody LoginRequest request) throws ApiException {
    return ecSiteAccountService.login(userId, request);
  }
}

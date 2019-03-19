package com.topcoder.api.controllers;

import com.topcoder.api.dao.ECSiteAccountDAO;
import com.topcoder.api.entity.LoginRequest;
import com.topcoder.api.entity.LoginResponse;
import com.topcoder.api.exceptions.AppException;
import com.topcoder.api.services.ECSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
  ECSiteService ecSiteService;


  /**
   * get all sites by user id
   *
   * @param userId the user id
   * @throws AppException if any error happened
   */
  @GetMapping("/{userId}/ec_sites")
  public List<ECSiteAccountDAO> getAllEcSites(@PathVariable("userId") String userId) throws AppException {
    return ecSiteService.getAll(userId);
  }

  /**
   * get ec site by site id
   *
   * @param userId the user id
   * @param id     the site id
   * @throws AppException if any error happened
   */
  @GetMapping("/{userId}/ec_sites/{siteId}")
  public ECSiteAccountDAO getECSite(@PathVariable("userId") String userId, @PathVariable("siteId") Integer id) throws AppException {
    return ecSiteService.getECSite(userId, id);
  }

  /**
   * update ec site by id
   *
   * @param userId the user id
   * @param id     the site id
   * @param entity the ec site request entity
   * @throws AppException if any error happened
   */
  @PutMapping("/{user_id}/ec_sites/{siteId}")
  public ECSiteAccountDAO updateECSite(@PathVariable("user_id") String userId,
                                       @PathVariable("siteId") Integer id,
                                       @RequestBody ECSiteAccountDAO entity) throws AppException {
    return ecSiteService.updateECSite(userId, id, entity);
  }

  /**
   * get login init things
   *
   * @param userId the user id
   * @param siteId the site id
   * @param uuid   the task id
   * @throws AppException if any error happened
   */
  @GetMapping("/{userId}/login_init")
  public LoginResponse loginInit(@PathVariable("userId") String userId,
                                 @RequestParam("siteId") Integer siteId,
                                 @RequestParam("uuid") String uuid)
    throws AppException {
    return ecSiteService.loginInit(userId, siteId, uuid);
  }

  /**
   * do login
   *
   * @param userId  the user id
   * @param request the login request
   * @throws AppException if any error happened
   */
  @PostMapping("/{userId}/login")
  public LoginResponse login(@PathVariable("userId") String userId, @RequestBody LoginRequest request) throws AppException {
    return ecSiteService.login(userId, request);
  }
}

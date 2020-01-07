package com.topcoder.api.service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.BadRequestException;
import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.api.service.login.LoginHandler;
import com.topcoder.api.service.login.LoginHandlerFactory;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.dao.UserDAO;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;

@Service
public class ECSiteAccountService {

  /**
   * user repository
   */
  @Autowired
  UserRepository userRepository;

  /**
   * ec site account repository
   */
  @Autowired
  ECSiteAccountRepository ecSiteAccountRepository;


  @Autowired
  AmazonProperty amazonProperty;

  @Autowired
  private LoginHandlerFactory loginHandlerFactory;
  
  /**
   * the logger
   */
  private Logger logger = LoggerFactory.getLogger(ECSiteAccountService.class.getName());

  /**
   * the BCryptPasswordEncoder passwordEncoder instance.
   */
  private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public List<ECSiteAccountDAO> getAll(String userId) throws ApiException {
    UserDAO userDAO = checkUserByCryptoID(userId);
    return ecSiteAccountRepository.findAllByUserId(userDAO.getId());
  }


  /**
   * get ECSiteAccountDAO by id
   *
   * @param userId the user id
   * @param id     the site id
   * @return the db ECSiteAccountDAO
   * @throws ApiException if not exist
   */
  public ECSiteAccountDAO getECSite(String userId, Integer id) throws ApiException {
    checkUserByCryptoID(userId);
    return get(id);
  }


  /**
   * update ECSiteAccountDAO
   *
   * @param userId the user id
   * @param id     the ECSiteAccountDAO id
   * @param entity the request entity
   * @return the updated
   * @throws ApiException if user not found or ECSiteAccountDAO not found
   */
  public ECSiteAccountDAO updateECSite(String userId, Integer id, ECSiteAccountDAO entity) throws ApiException {
    checkUserByCryptoID(userId);

    ECSiteAccountDAO accountDAO = get(id);
    accountDAO.setEcUseFlag(entity.getEcUseFlag());
    accountDAO.setUpdateAt(Date.from(Instant.now()));
    ecSiteAccountRepository.save(accountDAO);

    return accountDAO;
  }

  
  public LoginResponse loginInit(String userId, Integer siteId, String uuid) throws ApiException {
        
    UserDAO userDAO = checkUserByCryptoID(userId);
    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(siteId);

    LoginHandler handler = this.loginHandlerFactory.getLoginHandler(ecSiteAccountDAO.getEcSite());
    
    return handler.loginInit(userDAO.getId(), siteId, uuid);
  }

  public LoginResponse login(String userId, LoginRequest request) throws ApiException {
    
    UserDAO userDAO = checkUserByCryptoID(userId);
    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(request.getSiteId());
    
    LoginHandler handler = this.loginHandlerFactory.getLoginHandler(ecSiteAccountDAO.getEcSite());
    
    return handler.login(userDAO.getId(), request);
  }


  /**
   * get ECSiteAccountDAO by id
   *
   * @param id the id
   * @return the entity
   */
  public ECSiteAccountDAO get(int id) throws EntityNotFoundException {
    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(id);
    if (ecSiteAccountDAO == null) {
      throw new EntityNotFoundException("Not found ECSiteAccount where id = " + id);
    }
    return ecSiteAccountDAO;
  }

  /**
   * the BCryptPasswordEncoder passwordEncoder instance.
   */
  private UserDAO checkUserByCryptoID(String id) throws BadRequestException {
    Iterable<UserDAO> userDAOS = userRepository.findAll();

    UserDAO ret = null;
    for (UserDAO userDAO : userDAOS) {
      if (passwordEncoder.matches(userDAO.getId() + "", id)) {
        ret = userDAO;
      }
    }

    if (ret == null) {
      throw new BadRequestException("User not exist");
    }

    // expired
    if (ret.getIdExpireAt() == null || ret.getIdExpireAt().before(Date.from(Instant.now()))) {
      throw new BadRequestException("this url already expired");
    }
    return ret;
  }
}

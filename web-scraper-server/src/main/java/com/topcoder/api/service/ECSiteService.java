package com.topcoder.api.service;

import com.gargoylesoftware.htmlunit.util.Cookie;
import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.BadRequestException;
import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.common.config.AmazonProperty;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.dao.UserDAO;
import com.topcoder.common.model.AuthStatusType;
import com.topcoder.common.model.CrawlerContext;
import com.topcoder.common.model.ECCookie;
import com.topcoder.common.model.ECCookies;
import com.topcoder.common.model.LoginRequest;
import com.topcoder.common.model.LoginResponse;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.common.repository.UserRepository;
import com.topcoder.common.traffic.TrafficWebClient;
import com.topcoder.scraper.module.amazon.crawler.AmazonAuthenticationCrawler;
import com.topcoder.scraper.module.amazon.crawler.AmazonAuthenticationCrawlerResult;
import com.topcoder.scraper.service.WebpageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class ECSiteService {

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
  private ApplicationContext applicationContext;

  /**
   * save Crawler context
   */
  private Map<Integer, CrawlerContext> crawlerContextMap = new HashMap<>();

  /**
   * the logger
   */
  private Logger logger = LoggerFactory.getLogger(ECSiteService.class.getName());

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

    CrawlerContext context = crawlerContextMap.get(siteId);

    if (context == null || !context.getUuid().equals(uuid)) { // ignore previous context, because of uuid is different
      context = new CrawlerContext(new TrafficWebClient(userDAO.getId(), false),
        amazonProperty,
        applicationContext.getBean(WebpageService.class),
        uuid, null);
      context.setCrawler(new AmazonAuthenticationCrawler(ecSiteAccountDAO.getEcSite(),
        context.getProperty(),
        context.getWebpageService()));

      crawlerContextMap.put(siteId, context); // save context
    }

    try {
      AmazonAuthenticationCrawlerResult result = context.getCrawler().authenticate(context.getWebClient(),
        null, null, null, true);
      if (result.isSuccess()) {
        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), null, null,
          context.getCrawler().getAuthStep(), result.getReason());
      } else {
        ecSiteAccountDAO.setAuthStatus(AuthStatusType.FAILED);
        ecSiteAccountDAO.setAuthFailReason(result.getReason());
        ecSiteAccountRepository.save(ecSiteAccountDAO);
        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), result.getCodeType(), result.getImg(),
          context.getCrawler().getAuthStep(), result.getReason());
      }
    } catch (Exception e) { // here is fatal error, cannot continue
      ecSiteAccountDAO.setAuthStatus(AuthStatusType.FAILED);
      ecSiteAccountDAO.setAuthFailReason(e.getMessage());
      ecSiteAccountRepository.save(ecSiteAccountDAO);
      throw new ApiException(e.getMessage());
    }
  }


  public LoginResponse login(String userId, LoginRequest request) throws ApiException {
    checkUserByCryptoID(userId);

    ECSiteAccountDAO ecSiteAccountDAO = ecSiteAccountRepository.findOne(request.getSiteId());

    ecSiteAccountDAO.setPassword(request.getPassword());
    ecSiteAccountDAO.setLoginEmail(request.getEmail());
    ecSiteAccountRepository.save(ecSiteAccountDAO); // save it first


    CrawlerContext context = crawlerContextMap.get(request.getSiteId());
    if (context == null || !context.getUuid().equals(request.getUuid())) { // context error
      ecSiteAccountDAO.setAuthStatus(AuthStatusType.FAILED);
      ecSiteAccountDAO.setAuthFailReason("crawler context error");
      ecSiteAccountRepository.save(ecSiteAccountDAO);
      throw new BadRequestException(ecSiteAccountDAO.getAuthFailReason());
    }

    try {
      AmazonAuthenticationCrawlerResult result = context.getCrawler()
        .authenticate(context.getWebClient(), request.getEmail(),
          request.getPassword(), request.getCode(), false);

      if (result.isSuccess()) { // succeed , update status and save cookies

        ecSiteAccountDAO.setAuthStatus(AuthStatusType.SUCCESS);
        ecSiteAccountDAO.setAuthFailReason(null);
        List<ECCookie> ecCookies = new LinkedList<>();
        for (Cookie cookie : context.getWebClient().getWebClient().getCookieManager().getCookies()) {
          ECCookie ecCookie = new ECCookie();
          ecCookie.setName(cookie.getName());
          ecCookie.setDomain(cookie.getDomain());
          ecCookie.setValue(cookie.getValue());
          ecCookie.setExpires(cookie.getExpires());
          ecCookie.setHttpOnly(cookie.isHttpOnly());
          ecCookie.setPath(cookie.getPath());
          ecCookie.setSecure(cookie.isSecure());
          ecCookies.add(ecCookie);
        }
        ecSiteAccountDAO.setEcCookies(new ECCookies(ecCookies).toJSONString());
        ecSiteAccountRepository.save(ecSiteAccountDAO);

        return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), result.getCodeType(), result.getImg(),
          context.getCrawler().getAuthStep(), result.getReason());
      } else { // login failed
        ecSiteAccountDAO.setAuthStatus(AuthStatusType.FAILED);
        ecSiteAccountDAO.setAuthFailReason(result.getReason());
        ecSiteAccountRepository.save(ecSiteAccountDAO);
        if (result.isNeedContinue()) {
          return new LoginResponse(ecSiteAccountDAO.getLoginEmail(), result.getCodeType(), result.getImg(),
            context.getCrawler().getAuthStep(), result.getReason());
        } else { // cannot continue, throw error
          throw new ApiException(result.getReason());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      ecSiteAccountDAO.setAuthStatus(AuthStatusType.FAILED);
      ecSiteAccountDAO.setAuthFailReason(e.getMessage());
      ecSiteAccountRepository.save(ecSiteAccountDAO);
      throw new ApiException(e.getMessage());
    }
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

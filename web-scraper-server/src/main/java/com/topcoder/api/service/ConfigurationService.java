package com.topcoder.api.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.topcoder.api.exception.ApiException;
import com.topcoder.api.exception.EntityNotFoundException;
import com.topcoder.common.dao.ConfigurationDAO;
import com.topcoder.common.model.HtmlPath;
import com.topcoder.common.model.ProductInfo;
import com.topcoder.common.model.PurchaseHistory;
import com.topcoder.common.repository.ConfigurationRepository;
import com.topcoder.common.repository.ECSiteAccountRepository;
import com.topcoder.scraper.module.ecunifiedmodule.DryRunProductModule;
import com.topcoder.scraper.module.ecunifiedmodule.DryRunPurchaseHistoryModule;

/**
 * scraper service
 */
@Service
public class ConfigurationService {

  /**
   * the scraper Repository
   */
  @Autowired
  ConfigurationRepository configurationRepository;

  /**
   * ec site account repository
   */
  @Autowired
  ECSiteAccountRepository ecSiteAccountRepository;

  @Autowired
  DryRunPurchaseHistoryModule dryRunPurchaseHistoryModule;

  @Autowired
  DryRunProductModule dryRunProductModule;

  /**
   * get config by site and type
   *
   * @param site the ec site
   * @param type the logic type
   * @return the config text
   * @throws EntityNotFoundException if not found
   */
  public String getConfig(String site, String type) throws EntityNotFoundException {
	ConfigurationDAO configurationDAO = get(site, type);
    if (configurationDAO == null) {
      throw new EntityNotFoundException("Cannot found config where site = " + site + " and " + type);
    }
	return get(site, type).getConfig();
  }

  /**
   * create or update ScraperDAO
   *
   * @param site the ec site
   * @param type the logic type
   * @param entity the request entity
   * @return the result message text
   * @throws ApiException if any error happened
   */
  public String createOrUpdateConfiguration(String site, String type, String conf) throws ApiException {
	try {
	  String resultText = "success ";

	  ConfigurationDAO configurationDAO = get(site, type);

	  if (configurationDAO == null) {
	    configurationDAO = new ConfigurationDAO();
	    resultText += "create record to scraper table";
	  } else {
	    resultText += "update record to scraper table";
	  }

      configurationDAO.setSite(site);
      configurationDAO.setType(type);
      configurationDAO.setConfig(conf);
      configurationRepository.save(configurationDAO);

      return resultText;

    } catch(Exception e) {
      e.printStackTrace();
	  throw new ApiException("failed to create or update conf");
    }
  }

  /**
   * execute conf
   *
   * @param site the ec site
   * @param type the logic type
   * @param request to executable conf
   * @throws ApiException if any error happened
   */
  public List<Object> executeConfiguration(String site, String type, String conf) throws ApiException {
    try {

      List<Object> result = new ArrayList<>();
      List<String> sites = Arrays.asList(site);

      if (type.equals("purchase_history")) {
        // the case for get purchase history
    	  dryRunPurchaseHistoryModule.setConfig(conf);
        dryRunPurchaseHistoryModule.fetchPurchaseHistoryList(sites);
        List<PurchaseHistory> purchaseHistoryList = dryRunPurchaseHistoryModule.getPurchaseHistoryList();
        List<String> htmlPathList = dryRunPurchaseHistoryModule.getHtmlPathList();
        result.add(purchaseHistoryList);
        result.add(new HtmlPath(htmlPathList));
      } else if (type.equals("product")) {
        // the case for get product detail
        dryRunProductModule.fetchProductDetailList(sites);
        List<ProductInfo> productInfoList = dryRunProductModule.getProductInfoList();
        List<String> htmlPathList = dryRunProductModule.getHtmlPathList();
        result.add(productInfoList);
        result.add(new HtmlPath(htmlPathList));
      } else if (type.equals("")) {
        // the case for get product search
      } else {
        // other type
        throw new ApiException("the type:" + type + " was not supported");
      }

      return result;

    } catch(Exception e) {
      e.printStackTrace();
      throw new ApiException("failed to execute conf");
    }
  }

  /**
   *  get the html string
   *
   * @param the html file name
   * @return the html data
   * @throws ApiException if any error happened
   */
  public String getHtmlString(String htmlFileName) throws ApiException {
    try {
      String currentAbsolutePath = System.getProperty("user.dir");
      String htmlFilePath = searchHtmlFilePath(currentAbsolutePath + "/logs", htmlFileName);
      File htmlFile = new File(htmlFilePath);
      FileReader fileReader = new FileReader(htmlFile);
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      StringBuffer htmlData = new StringBuffer();
      String tempHtmlData = "";
      while ((tempHtmlData = bufferedReader.readLine()) != null) {
        htmlData.append(tempHtmlData);
      }
      bufferedReader.close();
      return htmlData.toString();
    } catch(FileNotFoundException e) {
      e.printStackTrace();
      throw new ApiException(htmlFileName + " does not exist");
    } catch(Exception e) {
      e.printStackTrace();
      throw new ApiException("failed to get html file data");
    }
  }

  /**
   * search html file from path
   *
   * @param the directory path
   * @param the html file name
   * @return the html file path
   */
  private String searchHtmlFilePath(String directoryPath, String htmlFileName) {
    String htmlFilePath = "";
    File directory = new File(directoryPath);
    File files[] = directory.listFiles();
    for (int i = 0; i < files.length; i++) {
      String directoryOrFileName = files[i].getName();
      if (files[i].isDirectory()){
        htmlFilePath =  searchHtmlFilePath(directoryPath + "/" + directoryOrFileName, htmlFileName);
      } else {
        if (directoryOrFileName.equals(htmlFileName + ".html")) {
          return directoryPath + "/" + directoryOrFileName;
        }
      }
    }
    return htmlFilePath;
  }

  /**
   * get ScraperDAO by site and type
   *
   * @param site the ec site
   * @param site the logic type
   * @return the ScraperDAO
   */
  public ConfigurationDAO get(String site, String type) {
	ConfigurationDAO configurationDAO = configurationRepository.findBySiteAndType(site, type);
    return configurationDAO;
  }

}

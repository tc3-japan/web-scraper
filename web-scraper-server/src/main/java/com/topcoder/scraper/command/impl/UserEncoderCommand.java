package com.topcoder.scraper.command.impl;

import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.dao.UserDAO;
import com.topcoder.common.model.AuthStatusType;
import com.topcoder.common.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Change detection init Command
 */
@Component
public class UserEncoderCommand {
  @Autowired
  private UserRepository userRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(UserEncoderCommand.class);

  public void run(ApplicationArguments args) {

    String outputFile   = "encryptedUserIds.txt";
    String url_base     = "";
    String url_base_tmp = "SCHEME_HOST_PORT/#/users/";
    String tail         = "/ec-site-settings";
    List<String> outputFiles = args.getOptionValues("output_file");
    List<String> url_bases   = args.getOptionValues("url_base");
    if (outputFiles != null) {
      outputFile = outputFiles.get(0);
    }
    if (url_bases != null) {
      url_base = url_base_tmp.replaceAll("SCHEME_HOST_PORT", url_bases.get(0));
    } else {
      url_base = url_base_tmp.replaceAll("SCHEME_HOST_PORT", "http://127.0.0.1:8085");
    }

    if (userRepository != null) {
      Iterable<UserDAO> userDAOS = userRepository.findAll();
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

      try {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, false)));

        // Do for all users.
        for (UserDAO userDAO : userDAOS) {
          boolean saveRecord = true;
          if (args.containsOption("get_failed_logins")) {
            saveRecord = false;
            for (ECSiteAccountDAO ecSiteAccountDAO : userDAO.getECSiteAccountDAOS()) {
              LOGGER.info(">>> User Auth Status: " + ecSiteAccountDAO.getAuthStatus());
              if (!AuthStatusType.SUCCESS.equals(ecSiteAccountDAO.getAuthStatus())) {
                saveRecord = true;
                LOGGER.info("Found user with failed login.");
              }
            }
          }

          // Save User
          if (saveRecord) {
            String idString        = Integer.toString(userDAO.getId());
            String encrpytedUserId = passwordEncoder.encode(idString);

            out.println(userDAO.getEmailForContact() + "," + url_base + "/" + encrpytedUserId + tail);
          }
        }
        out.close();
      } catch (IOException e) {
        LOGGER.info(e.getMessage());
      }

    } else {
      LOGGER.info("\n\nuserRepository is null! " + "\n\n");
    }
  }
}
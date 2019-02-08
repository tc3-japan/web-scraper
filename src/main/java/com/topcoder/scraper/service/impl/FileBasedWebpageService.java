package com.topcoder.scraper.service.impl;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.topcoder.scraper.service.WebpageService;
import com.topcoder.scraper.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File based WebpageService
 * which all records are saved as files in disk
 */
@Service
public class FileBasedWebpageService implements WebpageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileBasedWebpageService.class);
  private static final String OUT_PATH = "logs";

  @Override
  public String save(String filename, String site, String content) {
    String path = generateFilename(filename, site);
    try {
      FileUtils.write(new File(path), content, "UTF-8");
      LOGGER.info("Web page saved: " + path);
    } catch (IOException e) {
      LOGGER.error("Fail to write webpage file: " + path);
    }
    return path;
  }

  @Override
  public String saveImage(String filename, String fileExt, String site, HtmlImage htmlImage) {
    String path = generateImageFilename(filename, fileExt, site);
    try {
      ImageReader imageReader     = htmlImage.getImageReader();
      BufferedImage bufferedImage = imageReader.read(0);
      String formatName           = imageReader.getFormatName();
      FileOutputStream fileOutput = new FileOutputStream(path);
      ImageIO.write(bufferedImage, formatName, fileOutput);
      LOGGER.info("Image saved: " + path);
    } catch (IOException e) {
      LOGGER.error("Fail to write image file: " + path);
    }
    return path;
  }

  /**
   * generate filename
   * @param filename file name
   * @param site site name
   * @return absolute file path
   */
  private String generateFilename(String filename, String site) {

    return generateDirname(site) + File.separator +
            filename + "-" + DateUtils.currentDateTime() +
            ".html";
  }

  /**
   * generate image filename
   * @param filename image file name
   * @param fileExt image file extention
   * @param site site name
   * @return absolute file path
   */
  private String generateImageFilename(String filename, String fileExt, String site) {

    return generateDirname(site) + File.separator +
            filename + "-" + DateUtils.currentDateTime() +
            "." + fileExt;
  }

  /**
   * generate directory name
   * @param site site name
   * @return absolute file path
   */
  private String generateDirname(String site) {

    return new File("").getAbsoluteFile().getAbsolutePath() + File.separator +
            OUT_PATH + File.separator + site;
  }


}

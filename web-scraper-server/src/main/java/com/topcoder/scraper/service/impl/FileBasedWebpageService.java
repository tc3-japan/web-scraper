package com.topcoder.scraper.service.impl;

import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.topcoder.common.util.Common;
import com.topcoder.scraper.service.WebpageService;
import com.topcoder.common.util.DateUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

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
            String message = "Fail to write webpage file: " + path;
            Common.ZabbixLog(LOGGER, message, e);
        }
        return path;
    }

    @Override
    public String save(String filename, String site, String content, boolean saveFlag) {
        if (saveFlag) {
            return this.save(filename, site, content);
        } else {
            return null;
        }
    }

    @Override
    public String saveImage(String filename, String fileExt, String site, HtmlImage htmlImage) {
        String path = generateImageFilename(filename, fileExt, site);
        try {
            ImageReader imageReader = htmlImage.getImageReader();
            BufferedImage bufferedImage = imageReader.read(0);
            String formatName = imageReader.getFormatName();
            FileOutputStream fileOutput = new FileOutputStream(path);
            ImageIO.write(bufferedImage, formatName, fileOutput);
            LOGGER.info("Image saved: " + path);
        } catch (IOException e) {
            String message = "Fail to write image file: " + path;
            Common.ZabbixLog(LOGGER, message, e);
        }
        return path;
    }

    /**
     * save image to base64
     *
     * @param htmlImage the html image
     * @return
     */
    @Override
    public String toBase64Image(HtmlImage htmlImage) {
        try {
            ImageReader imageReader = htmlImage.getImageReader();
            BufferedImage bufferedImage = imageReader.read(0);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException e) {
            Common.ZabbixLog(LOGGER, "convert image to base64 failed", e);
        }
        return null;
    }

    /**
     * generate filename
     *
     * @param filename file name
     * @param site     site name
     * @return absolute file path
     */
    private String generateFilename(String filename, String site) {

        return generateDirname(site) + File.separator +
                filename + "-" + DateUtils.currentDateTime() +
                ".html";
    }

    /**
     * generate image filename
     *
     * @param filename image file name
     * @param fileExt  image file extention
     * @param site     site name
     * @return absolute file path
     */
    private String generateImageFilename(String filename, String fileExt, String site) {

        return generateDirname(site) + File.separator +
                filename + "-" + DateUtils.currentDateTime() +
                "." + fileExt;
    }

    /**
     * generate directory name
     *
     * @param site site name
     * @return absolute file path
     */
    private String generateDirname(String site) {

        return new File("").getAbsoluteFile().getAbsolutePath() + File.separator +
                OUT_PATH + File.separator + site;
    }


}

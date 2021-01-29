package com.topcoder.common.util;

import java.beans.FeatureDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.topcoder.common.dao.ECSiteAccountDAO;
import com.topcoder.common.model.AuthStatusType;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * common util class
 */
public class Common {

    /**
     * logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(Common.class.getName());

    /**
     * restore Cookies from ec site to web client
     *
     * @param webClient        the web client instance
     * @param ecSiteAccountDAO the ec site account
     * @return the result
     */
    public static boolean restoreCookies(WebClient webClient, ECSiteAccountDAO ecSiteAccountDAO) {

        webClient.getCookieManager().clearCookies();
        if (ecSiteAccountDAO.getAuthStatus() == null || ecSiteAccountDAO.getAuthStatus().equals(AuthStatusType.FAILED)) {
            logger.warn("skip ecSite id = " + ecSiteAccountDAO.getId() + ", because of auth status is failed");
            return false;
        }

        // restore cookies
        try {
            byte[] byteCookies = ecSiteAccountDAO.getEcCookies();
            ByteArrayInputStream bin = new ByteArrayInputStream(byteCookies);
            ObjectInputStream oin = new ObjectInputStream(bin);
            Set<Cookie> cookies = (Set<Cookie>) oin.readObject();
            bin.close();
            oin.close();
            for (Cookie cookie : cookies) {
                webClient.getCookieManager().addCookie(cookie);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        logger.info("Restore Cookie Successful");

        return true;

    /*
    String stringCookies = ecSiteAccountDAO.getEcCookies();
    ECCookies ecCookies = ECCookies.fromJSON(stringCookies);
    if (ecCookies == null || ecCookies.getCookies() == null || ecCookies.getCookies().size() <= 0) {
      logger.warn("skip ecSite id = " + ecSiteAccountDAO.getId() + ", because of parse cook failed");
      return false;
    }

    logger.info("Cookie Information: ");
    for (ECCookie ecCookie : ecCookies.getCookies()) {
      logger.info("* Cookie: " + ecCookie.getName() + "," + ecCookie.getValue()+ "," + ecCookie.getExpires() +": Restore");

      webClient.getCookieManager().addCookie(new Cookie(
              ecCookie.getDomain(), ecCookie.getName(), ecCookie.getValue(), ecCookie.getPath(), ecCookie.getExpires(),
              ecCookie.isSecure(), ecCookie.isHttpOnly()
      ));
    }
    */
    }

    /**
     * put cookies into request
     *
     * @param webClient the web client
     * @param url       the url
     */
    public static WebRequest wrapURL(WebClient webClient, String url) throws MalformedURLException {
        StringBuilder cookies = new StringBuilder();
        for (Cookie cookie : webClient.getCookieManager().getCookies()) {
            cookies.append(cookie.getName()).append("=").append(cookie.getValue()).append("; ");
        }
        WebRequest page = new WebRequest(new URL(url));
        page.setAdditionalHeader("cookie", cookies.substring(0, cookies.length() - 2));
        return page;
    }

    /**
     * get default value
     *
     * @param value        the value
     * @param defaultValue the default value
     * @param <T>          the type
     * @return the value
     */
    public static <T> T getValueOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * @param numText
     * @return float value
     */
    public static Float toFloat(String numText) {
        if (numText == null) {
            return null;
        }
        return Float.valueOf(numText
                .replaceAll("０", "0").replaceAll("１", "1").replaceAll("２", "2")
                .replaceAll("３", "3").replaceAll("４", "4").replaceAll("５", "5")
                .replaceAll("６", "6").replaceAll("７", "7").replaceAll("８", "8")
                .replaceAll("９", "9").replaceAll("[^\\d.]", ""));
    }

    static String[] HALF_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!\"#$%&'()--=^~¥\\|@`[{;+:*]},<.>/?_,."
            .split("");
    static String[] FULL_CHARS = "０１２３４５６７８９ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ！”＃＄％＆’（）−ー＝＾〜￥＼｜＠｀［｛；＋：＊］｝，＜．＞／？＿、。"
            .split("");
    static Map<String, Integer> HALF_CHARS_MAP = new HashMap<>();
    static Map<String, Integer> FULL_CHARS_MAP = new HashMap<>();

    static {
        for (int i = 0; i < HALF_CHARS.length; i++) {
            HALF_CHARS_MAP.put(HALF_CHARS[i], i);
        }
        for (int i = 0; i < FULL_CHARS.length; i++) {
            FULL_CHARS_MAP.put(FULL_CHARS[i], i);
        }
    }

    public static String toHalf(String text) {
        if (text == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] chars = text.split("");
        for (int i = 0; i < chars.length; i++) {
            Integer idx = FULL_CHARS_MAP.get(chars[i]);
            sb.append(idx == null ? (HALF_CHARS_MAP.containsKey(chars[i]) ? chars[i] : " ") : HALF_CHARS[idx]);
        }
        //return Normalizer.normalize(text, Normalizer.Form.NFKC);
        return sb.toString().trim();
    }

    public static String normalize(String code) {
        if (code == null) {
            return null;
        }
        String tmp = toHalf(code);
        return tmp == null ? null : tmp.replaceAll("[\\p{Punct}¥]+", "-");
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static String toJSON(Object obj) {
        if (obj == null)
            return "";
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            ZabbixLog(logger, "Failed to convert object to JSON.", e);
            return null;
        }
    }

    /**
     * Get the entity null properties names.
     *
     * @param source the entity
     * @return the null properties names.
     */
    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public static void main(String[] args) {
        String text = "０１２３４５６７８９ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ！”＃＄％＆’（）−ー＝＾〜￥＼｜＠｀［｛；＋：＊］｝，＜．＞／？＿、。";
        System.out.println(text);
        text = Common.toHalf(text);
        System.out.println(text);
        text = text.replaceAll("[\\p{Punct}¥]+", "-");
        System.out.println(text);
    }

    public static void ZabbixLog(Logger logger, String message, Throwable e) {
        logger.error("Zabbix [" + message + ":" + getThrowableMessage(e) + "]");
        e.printStackTrace();
    }

    public static void ZabbixLog(Logger logger, String message) {
        logger.error("Zabbix [" + message + "]");
    }

    public static void ZabbixLog(Logger logger, Throwable e) {
        ZabbixLog(logger, getThrowableMessage(e));
        e.printStackTrace();
    }

    private static String getThrowableMessage(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }
        return e.getMessage();
    }

}

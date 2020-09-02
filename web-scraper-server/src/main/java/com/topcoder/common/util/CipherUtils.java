package com.topcoder.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CipherUtils {
  // see: https://blogs.yahoo.co.jp/dk521123/34330480.html

  private static Logger logger = LoggerFactory.getLogger(Common.class.getName());

  private static final String ALGORITHM = "AES";                 // Encryption method AES(Advanced Encryption Standard)
  private static final int    AES_BYTES = 16;                    // AES Byte number
  //private static final String SECRET_KEY = "1234567890123456"; // 16 characters Key of Encryption method AES
  private static final String SECRET_KEY = "{{SECRET_KEY}}";     // 16 characters Key of Encryption method AES, normalized when used.

  /**
   * encrypt-AES and encode-Base64:
   * encrypt string by AES and encode it by Base64
   */
  public static String encrypt(String originalSource) {
    if (originalSource == null) return null;

    byte[] originalBytes = originalSource.getBytes();
    byte[] encryptBytes = cipher(Cipher.ENCRYPT_MODE, originalBytes);
    byte[] encryptBytesBase64 = Base64.getEncoder().encode(encryptBytes);
    return new String(encryptBytesBase64);
  }

  /**
   * decode-Base64 and decrypt-AES:
   * decrypt from Base64 string that is encrypted by AES to original string
   */
  public static String decrypt(String encryptBytesBase64String) {
    if (encryptBytesBase64String == null) return null;

    byte[] encryptBytes = Base64.getDecoder().decode(encryptBytesBase64String);
    byte[] originalBytes = cipher(Cipher.DECRYPT_MODE, encryptBytes);
    return new String(originalBytes);
  }

  /**
   * encrypt / decrypt common logic
   * cryptMode: Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE
   */
  private static byte[] cipher(int cryptMode, byte[] source) {
    String normalizedKey;
    if (SECRET_KEY.length() >= AES_BYTES) {
      normalizedKey = SECRET_KEY.substring(0, AES_BYTES);
    } else {
      normalizedKey = String.format("%" + AES_BYTES + "s", SECRET_KEY).replace(" ", "0");
    }
    //logger.info("SECRET_KEY: " + SECRET_KEY );
    //logger.info("normalizedKey: " + normalizedKey );
    byte[] secretKeyBytes = normalizedKey.getBytes();

    try {
      SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyBytes, ALGORITHM);
      Cipher cipher = Cipher.getInstance(ALGORITHM);
      cipher.init(cryptMode, secretKeySpec);

      // cipher success
      return cipher.doFinal(source);
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
            | IllegalBlockSizeException | BadPaddingException e ) {
      // cipher failure
      logger.info("Cipher fail. Exception occurs.");
      logger.info("Exception:" + e.toString());
      return source;
    }
  }

  public static String md5(String source) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] md5Byte = md5.digest(source.getBytes("UTF-8"));
      return toHexStr(md5Byte);
    } catch(Exception e) {
      logger.error("Failed to generate MD5 hash", e);
      return null;
    }
  }

  public static String toHexStr(byte[] bytes) {
    if (bytes == null) {
      return null;
    }
    if (bytes.length == 0) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for(byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}

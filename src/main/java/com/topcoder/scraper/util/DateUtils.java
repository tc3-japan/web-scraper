package com.topcoder.scraper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class DateUtils {
  public static String currentDateTime() {
    return LocalDateTime.now().toString().replace(":", "-");
  }

  public static Date fromString(String in) throws ParseException {
    return new SimpleDateFormat("MMM dd, yyyy").parse(in);
  }
}

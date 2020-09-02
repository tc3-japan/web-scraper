package com.topcoder.scraper.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class ECSiteService {

  public Set<String> getAllECSites() {
    return new HashSet<>(Arrays.asList("amazon", "yahoo", "rakuten"));
    //return new HashSet<>(Arrays.asList("amazon", "yahoo"));
  }
}

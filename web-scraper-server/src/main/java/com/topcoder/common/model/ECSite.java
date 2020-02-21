package com.topcoder.common.model;

import com.topcoder.scraper.Consts;

public enum ECSite {
	  AMAZON(Consts.EC_SITE_AMAZON),
	  KOJIMA(Consts.EC_SITE_KOJIMA),;
	  // TODO : add Yahoo, Rakuten

	  private final String value;

	  private ECSite(String value){
		  this.value = value;
	  }

	  public String getValue() {
		  return this.value;
	  }

	  public static ECSite getType(String value) {
		  ECSite[] types = ECSite.values();
	        for (ECSite type : types) {
	            if (type.getValue().equals(value)) {
	                return type;
	            }
	        }
	        return null;
	    }
}

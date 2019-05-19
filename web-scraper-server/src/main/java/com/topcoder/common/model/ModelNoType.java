package com.topcoder.common.model;

import com.topcoder.scraper.Consts;

public enum ModelNoType {
	  PRODUCT(0, Consts.AMAZON_PRODUCT_DETAIL_MODEL_NO_PRODUCT),
	  MAKER(1, Consts.AMAZON_PRODUCT_DETAIL_MODEL_NO_MAKER),
	  MODEL(2, Consts.AMAZON_PRODUCT_DETAIL_MODEL_NO_MODEL),;
	  
	  private final int key;
	  
	  private final String value;
	  
	  private ModelNoType(int key, String value){
		  this.key = key;
		  this.value = value;
	  }
	  
	  public int getKey() {
		  return this.key;
	  }
	  
	  public String getValue() {
		  return this.value;
	  }
	  
	  public static ModelNoType getType(final int key) {
		  ModelNoType[] types = ModelNoType.values();
	        for (ModelNoType type : types) {
	            if (type.getKey() == key) {
	                return type;
	            }
	        }
	        return null;
	    }
}

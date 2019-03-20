package com.topcoder.scraper.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.common.model.ProductInfo;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class JpaConverterPurchaseInfoJson implements AttributeConverter<ProductInfo, String> {

  private final static ObjectMapper OB = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(ProductInfo meta) {
    return meta.toJson();
  }

  @Override
  public ProductInfo convertToEntityAttribute(String dbData) {
    return ProductInfo.fromJson(dbData);
  }
}

package com.topcoder.scraper.converter;

import com.topcoder.common.model.PurchaseHistory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class JpaConverterPurchaseHistoryJson implements AttributeConverter<PurchaseHistory, String> {

  @Override
  public String convertToDatabaseColumn(PurchaseHistory meta) {
    return meta.toJson();
  }

  @Override
  public PurchaseHistory convertToEntityAttribute(String dbData) {
    return PurchaseHistory.fromJson(dbData);
  }
}

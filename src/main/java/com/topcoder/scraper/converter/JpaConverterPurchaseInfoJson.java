package com.topcoder.scraper.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.scraper.model.ProductInfo;
import java.io.IOException;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class JpaConverterPurchaseInfoJson implements AttributeConverter<ProductInfo, String> {

    private final static ObjectMapper OB = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ProductInfo meta) {
        try {
            return OB.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public ProductInfo convertToEntityAttribute(String dbData) {
        try {
            return OB.readValue(dbData, ProductInfo.class);
        } catch (IOException ex) {
            return null;
        }
    }
}

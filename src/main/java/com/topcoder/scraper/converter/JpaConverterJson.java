package com.topcoder.scraper.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcoder.scraper.model.PurchaseHistory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;

@Converter(autoApply = true)
public class JpaConverterJson implements AttributeConverter<PurchaseHistory, String> {

    private final static ObjectMapper OB = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(PurchaseHistory meta) {
        try {
            return OB.writeValueAsString(meta);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public PurchaseHistory convertToEntityAttribute(String dbData) {
        try {
            return OB.readValue(dbData, PurchaseHistory.class);
        } catch (IOException ex) {
            return null;
        }
    }
}

package com.techartworks.userfederation.storage.converter;

import org.jboss.logging.Logger;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;

@Converter
public class LocalDateAttributeConverter implements AttributeConverter<String, Date> {

    private static final Logger log = Logger.getLogger(LocalDateAttributeConverter.class);

    @Override
    public Date convertToDatabaseColumn(String locDate) {
        log.info("LocalDateAttributeConverter: convertToDatabaseColumn ->" + locDate);
        return locDate == null ? null : Date.valueOf(locDate);
    }

    @Override
    public String convertToEntityAttribute(Date sqlDate) {
        log.info("LocalDateAttributeConverter: convertToEntityAttribute ->" + sqlDate);
        return sqlDate == null ? null : sqlDate.toString();
    }
}

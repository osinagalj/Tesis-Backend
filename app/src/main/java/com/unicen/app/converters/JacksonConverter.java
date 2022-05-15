package com.unicen.app.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.IOException;
import java.util.Map;

@Converter
public class JacksonConverter implements AttributeConverter<Map<Object, String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(JacksonConverter.class);

    @Override
    public String convertToDatabaseColumn(Map<Object, String> information) {
        String json = null;

        try {
            json = objectMapper.writeValueAsString(information);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return json;
    }

    @Override
    public Map<Object, String> convertToEntityAttribute(String information) {
        Map<Object, String> parsedObject = null;

        try {
            parsedObject = objectMapper.readValue(information, Map.class);
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }

        return parsedObject;
    }

}

package com.ecofy.core.dto.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Util class for encoding/decoding objects into Base64 using JSON format
 */
public class Base64JSONObjectEncoder {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String encode(Object object) throws JsonProcessingException {
        String serializedObject = mapper.writeValueAsString(object);
        return Base64.getEncoder().encodeToString(serializedObject.getBytes());
    }

    public static <T> T decode(String encodedObject, Class<T> objectClass) throws IOException {
        byte[] serializedObject = Base64.getDecoder().decode(encodedObject.getBytes(StandardCharsets.UTF_8));
        return mapper.readValue(serializedObject, objectClass);
    }

}

package com.unicen.core.services;

import com.unicen.core.exceptions.CoreApiException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component("environmentPropertiesService")
public class EnvironmentPropertiesService {

    private final Environment env;

    public EnvironmentPropertiesService(Environment env) {
        this.env = env;
    }

    protected String property(String propertyName) {
        String value = env.getProperty(propertyName);
        if (value == null || value.trim().equals("")) {
            throw CoreApiException.objectNotFound("Property " + propertyName + " not defined");
        }
        return value;
    }

    public Boolean isDevEnvironment(){
        var a = "dev".equals(Arrays.stream(this.env.getActiveProfiles()).findFirst().get());
        return "dev".equals(Arrays.stream(this.env.getActiveProfiles()).findFirst().get());
    }

    protected String property(String propertyName, String defaultValue) {
        String value = env.getProperty(propertyName);
        if (value == null || value.trim().equals("")) {
            return defaultValue;
        }
        return value;
    }

    protected boolean booleanProperty(String propertyName) {
        return Boolean.parseBoolean(property(propertyName));
    }

    protected List<String> listProperty(String propertyName) {
        return List.of(property(propertyName).split(","));
    }

    protected String resourceAsString(String path) {
        try {
            return IOUtils.toString(getClass().getClassLoader().getResource(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw CoreApiException.resourceCannotBeLoaded(path);
        }
    }

}

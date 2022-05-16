package com.unicen.core.services;

import com.unicen.core.configuration.AppEnvironment;
import com.unicen.core.exceptions.CoreApiException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Component("environmentPropertiesService")
public class EnvironmentPropertiesService {

    public static final String AWS_SECRETS_ARN_PROPERTY = "aws.secret.manager.secret.arn";
    public static final String OAUTH_ENABLED_PROPERTY = "core.ensolvers.oauth.enabled";
    public static final String TOKEN_CLEANING_PROCESSOR_ENABLED = "core.ensolvers.token.cleaning.processor.enabled";

    // method list that shouldn't be checked
    private List<String> INTERNAL_METHODS = List.of("property", "booleanProperty", "listProperty", "resourceAsString", "ensureValues");

    private final Environment env;
    private final SecretsService secretsService;

    public EnvironmentPropertiesService(Environment env, @Autowired(required = false) SecretsService secretsService) {
        this.env = env;
        this.secretsService = secretsService;
    }

    public AppEnvironment getEnv() {
        return AppEnvironment.valueOf(property("env").toUpperCase(Locale.ROOT));
    }

    public String getSecretARN() {
        return property(AWS_SECRETS_ARN_PROPERTY, "");
    }

    public String getImagesBucket() {
        return property("aws.images.bucket");
    }

    protected String property(String propertyName) {
        String value = env.getProperty(propertyName);
        if (value == null || value.trim().equals("")) {
            throw CoreApiException.objectNotFound("Property " + propertyName + " not defined");
        }
        return value;
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

    protected boolean booleanProperty(String propertyName, boolean defaultValue) {
        return Boolean.parseBoolean(property(propertyName, Boolean.toString(defaultValue)));
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

    // TODO properties below should we moved to ApplicationEnvironmentProperties - i.e. to ensolvers-core-backend-api
    public boolean isOAuthEnabled() {
        return booleanProperty(OAUTH_ENABLED_PROPERTY);
    }

}

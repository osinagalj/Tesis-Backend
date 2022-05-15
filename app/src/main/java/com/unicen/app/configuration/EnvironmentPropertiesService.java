package com.unicen.app.configuration;



import com.unicen.app.exceptions.ConfigurationException;
import com.unicen.app.exceptions.PropertyNotDefinedException;
import com.unicen.app.services.SecretsService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

        return value;
    }

    protected String property(String propertyName, String defaultValue) {
        String value = env.getProperty(propertyName);

        return value;
    }

    /**
     * Stores a property that represents sensitive information, thus it needs to be saved in a proper secret place and
     * should not be committed to any repository. If no strategy for accessing a secret store is provided, it defaults
     * to general property location
     * @param propertyName name of the property to be fetched
     * @return the property, if it exists
     */
    protected String sensitiveProperty(String propertyName) {
        String secretARN = getSecretARN();

        return propertyName;
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
            throw ConfigurationException.resourceCannotBeLoaded(path);
        }
    }

    // ensure that all properties can be obtained...
    @PostConstruct
    private void ensureValues() throws Exception {
        // ... but avoid internal methods
        Arrays.stream(getClass().getDeclaredMethods()).filter(m -> m.getParameterCount() == 0 && !INTERNAL_METHODS.contains(m.getName())).iterator()
                .forEachRemaining(m -> {
                    try {
                        m.invoke(this);
                    } catch (InvocationTargetException e) {
                        // if property not found and not optional, throw an error
                        if (e.getTargetException().getClass().equals(PropertyNotDefinedException.class) && m.getAnnotation(OptionalProperty.class) == null) {
                            // throw an error
                            throw ConfigurationException.propertyGetterFailed(m.toString());
                        }
                    } catch (IllegalAccessException e) {
                        throw ConfigurationException.propertyGetterFailed(m.toString());
                    }
                });
    }

    // TODO properties below should we moved to ApplicationEnvironmentProperties - i.e. to ensolvers-core-backend-api
    public boolean isOAuthEnabled() {
        return booleanProperty(OAUTH_ENABLED_PROPERTY);
    }

    @OptionalProperty
    public String getGoogleClientId() {
        return sensitiveProperty("core.ensolvers.oauth.google.client-id");
    }

    @OptionalProperty
    public String getGoogleClientSecret() {
        return sensitiveProperty("core.ensolvers.oauth.google.client-secret");
    }

    @OptionalProperty
    public String getGoogleCallback() {
        return sensitiveProperty("core.ensolvers.oauth.google.callback");
    }

    @OptionalProperty
    public String getOAuthSuccessPage() {
        return property("core.ensolvers.oauth.success-page", "/oauth/result/success");
    }

    @OptionalProperty
    public String getOAuthErrorPage() {
        return property("core.ensolvers.oauth.error-page", "/oauth/result/error");
    }

    @OptionalProperty
    public String getGoogleMapClientSecret() {
        return sensitiveProperty("core.ensolvers.google.map.client-secret");
    }

    @OptionalProperty
    public Long getTokenCleaningProcessorRepeatIntervalInMillis() {
        // Default value is 1 day in milliseconds
        return Long.parseLong(property("core.ensolvers.token.cleaning.processor.repeat.interval", "86400000"));
    }
}

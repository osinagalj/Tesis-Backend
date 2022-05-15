package com.unicen.core.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationPropertiesService extends EnvironmentPropertiesService {

    public ApplicationPropertiesService(Environment env, @Autowired(required = false) SecretsService secretsService) {
        super(env, secretsService);
    }

    public String getFromEmail() {
        return property("application.from-email");
    }

    public String getBaseUrl() {
        return property("application.base-url");
    }

    public String getValidationBaseUrl() {
        String defaultValidationBaseUrl = "http://localhost:8080/auth/validate";
        return property("application.validation.base-url", defaultValidationBaseUrl);
    }

    public String getValidationForgotPassword() {
        String defaultValidationForgotPassword = "http://localhost:8080/auth/validate-forgot-password";
        return property("application.validation.forgot-password", defaultValidationForgotPassword);
    }

    public boolean isSignUpEnabled() {
        return booleanProperty("application.sign-up.enabled");
    }

    public String getValidationForgotPasswordTemplate() {
        return resourceAsString("templates/validation-forgot-password.html");
    }

    public String getValidationEmailTemplate() {
        return resourceAsString("templates/validation-email.html");
    }

    public String getPasswordUpdateEmailTemplate() {
        return resourceAsString("templates/password-update.html");
    }

    public String getLoginEmailTemplate() {
        return resourceAsString("templates/login-email.html");
    }

    public List<String> getAuthorizedRequestUris() {
        return listProperty("app.oauth2.authorizedRedirectUris");
    }

    public boolean adminEndpointsEnabled() {
        return booleanProperty("application.admin-endpoints.enabled", false);
    }

}
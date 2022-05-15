package com.unicen.app.services;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.unicen.app.configuration.ApplicationPropertiesService;
import com.unicen.app.configuration.OAuthFlowAdapter;
import com.unicen.app.exceptions.ValidationException;
import com.unicen.app.model.AuthenticationToken;
import com.unicen.app.model.OAuthUserInformation;
import com.unicen.app.model.User;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@Service
public class OAuthService {


    private final AuthenticationService authenticationService;
    private ApplicationPropertiesService properties;
    private final OAuthFlowAdapter oauthFlowAdapter;

    private final ObjectMapper objectMapper;

    public OAuthService(AuthenticationService authenticationService, @Autowired(required = false) OAuthFlowAdapter oauthFlowAdapter,
            ApplicationPropertiesService properties) {
        this.authenticationService = authenticationService;
        this.properties = properties;
        this.oauthFlowAdapter = oauthFlowAdapter != null ? oauthFlowAdapter : new DefaultOAuthFlowAdapter();
        this.objectMapper = new ObjectMapper();
    }

    // TODO this is done this because only is instantiated if the correct
    //      it is requested - because we might not have all the properties
    //      we need for this. Ideally this should be instantiated one, depending
    //      on configuration properties and in a separated bean
    private OAuth20Service buildGoogleService() {
        String callbackUrl = properties.getBaseUrl() + "/oauth/callback/google";
        final OAuth20Service service = new ServiceBuilder(properties.getGoogleClientId()).apiSecret(properties.getGoogleClientSecret())
                .defaultScope("https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile") // replace with desired scope
                .callback(callbackUrl).build(GoogleApi20.instance());
        return service;
    }

    /**
     * Generates a fresh authorization URL for Google OAuth 2.0 flow
     * @return the URL generated
     */
    public String generateAuthorizationUrlForGoogle() {
        final OAuth20Service service = buildGoogleService();

        return service.createAuthorizationUrlBuilder().state("secret" + new Random().nextInt(999_999))
                .additionalParams(Map.of("access_type", "offline", "prompt", "consent")).build();
    }

    /**
     * Given a code obtained via the Google OAuth 2.0 process, it returns the

     * user
     * @return the user information
     */
    public OAuthUserInformation getUserInformationFromGoogleAccessCode(String code)
            throws IOException, ExecutionException, InterruptedException {
        OAuth20Service service = buildGoogleService();
        OAuth2AccessToken accessToken = service.getAccessToken(code);

        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo?alt=json");
        service.signRequest(accessToken, request);

        try (Response response = service.execute(request)) {
            if (response.getCode() == 200) {
                JsonNode node = this.objectMapper.readTree(response.getBody());
                String email = node.get("email").textValue();
                String firstName = node.get("given_name").textValue();
                String lastName = node.get("family_name").textValue();
                return new OAuthUserInformation(email, firstName, lastName);
            } else {
                throw new IOException("User is not allowed to log in: ");
            }
        }

    }

    /**
     * Given a Google OAuth 2.0 code, it uses to obtain user information and create a new
     * User in Core based on it. If the user exists, only a new authentication token
     * is issued
     * @param code the Google OAuth 2.0 code
     * @return the AuthenticationToken created for a new user
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public AuthenticationToken processGoogleAuthentication(String code) throws IOException, ExecutionException, InterruptedException {
        OAuthUserInformation info = getUserInformationFromGoogleAccessCode(code);

        if (!oauthFlowAdapter.acceptLogin(info.getEmail())) {
            throw new IOException("User is not allowed to log in: " + info.getEmail());
        }

        AuthenticationToken authenticationToken = authenticationService.ensureUserAndToken(info.getFirstName(), info.getLastName(), info.getEmail());
        authenticationService.setLastLogin(authenticationToken.getUser());
        ensureUserEnabled(authenticationToken.getUser());

        oauthFlowAdapter.afterLogin(authenticationToken);
        return authenticationToken;
    }

    /**
     * Method to execute logout behaviour by default with a hook in case that it's necessary to use
     * the information of the token before the action is executed.
     *
     * @param token
     */
    @Transactional
    public void logout(AuthenticationToken token) {
        this.oauthFlowAdapter.beforeLogout(token);
    }

    private void ensureUserEnabled(User user) {
        if (user.disabled()) {
            throw new ValidationException("User " + user.getEmail() + " is not enabled");
        }
    }

    private static class DefaultOAuthFlowAdapter implements OAuthFlowAdapter {


        @Override
        public boolean acceptLogin(String email) {
            return true;
        }

        @Override
        public void afterLogin(AuthenticationToken token) {

        }

        @Override
        public void beforeLogout(AuthenticationToken token){
        }
    }
}

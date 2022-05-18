package com.unicen.core.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;
import com.unicen.core.model.DtoAble;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public abstract class EnsolversOAuthService {

    protected OAuthService oAuthService;
    protected final String BASIC_URL;
    protected final String INTEGRATION_ID;
    protected final String INTEGRATION_SECRET;
    protected final String APP_URL;

    /**
     * The SocialProfileRequestDTO holds what the APIs gave as response, usually a json file that
     * should be marshalled. Since it's so short lived, this class has nothing to do outside of the
     * RequestManager, that's why it's private.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    protected static class SocialProfileRequestDTO {
        @JsonProperty("id") private String id;
        @JsonProperty("email") private String email;
        @JsonProperty("name") private String name;
        @JsonProperty("given_name") private String givenName;
        @JsonProperty("family_name") private String familyName;
        @JsonProperty("picture") private String picture;
        @JsonProperty("locale") private String locale;
        @JsonProperty("hd") private String hd;
        @JsonProperty("verified_email") private Boolean verifiedEmail;
    }

    public EnsolversOAuthService(String basicUrl, String integrationId, String integrationSecret, String appUrl) {
        BASIC_URL = basicUrl;
        INTEGRATION_ID = integrationId;
        INTEGRATION_SECRET = integrationSecret;
        APP_URL = appUrl;
        this.configureOauthService();
    }

    protected abstract void configureOauthService();

    public abstract DtoAble login(List<String> parameters) throws Exception;

    protected SocialProfileRequestDTO signRequest(Verb requestType, String requestEndpoint, OAuth2AccessToken accessToken) throws Exception {
        OAuthRequest request = new OAuthRequest(requestType, requestEndpoint);
        ((OAuth20Service) oAuthService).signRequest(accessToken, request);
        Response response = ((OAuth20Service) oAuthService).execute(request);

        return parseResponse(response.getBody());
    }

    protected abstract SocialProfileRequestDTO parseResponse(String data) throws Exception;
}

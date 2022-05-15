package com.unicen.app.controller;


import com.unicen.app.configuration.ApplicationPropertiesService;
import com.unicen.app.configuration.EnvironmentPropertiesService;
import com.unicen.app.dto.ApiResultDTO;
import com.unicen.app.dto.UserModelDTO;
import com.unicen.app.dto.utils.Base64JSONObjectEncoder;
import com.unicen.app.dto.utils.DTOMapper;
import com.unicen.app.model.AuthenticationToken;
import com.unicen.app.model.GenericSuccessResponse;
import com.unicen.app.services.AuthenticationService;
import com.unicen.app.services.OAuthService;
import com.unicen.app.utils.ControllerUtils;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@ConditionalOnProperty(EnvironmentPropertiesService.OAUTH_ENABLED_PROPERTY)
@RequestMapping("/oauth")
@Controller
public class OAuthController {



    private final ApplicationPropertiesService properties;
    private final OAuthService oauthService;
    private final AuthenticationService authenticationService;

    public OAuthController(ApplicationPropertiesService properties, OAuthService oauthService, AuthenticationService authenticationService) {
        this.properties = properties;
        this.oauthService = oauthService;
        this.authenticationService = authenticationService;
    }

    @PermitAll()
    @GetMapping("/connect/google")
    public String connectWithGoogle() {
        return "redirect:" + oauthService.generateAuthorizationUrlForGoogle();
    }

    @PermitAll()
    @GetMapping("/callback/google")
    public String googleCallback(@RequestParam String code) throws IOException, ExecutionException, InterruptedException {
        AuthenticationToken authenticationToken = null;

        try {
            authenticationToken = oauthService.processGoogleAuthentication(code);
            UserModelDTO userPublicData = DTOMapper.get().map(authenticationToken.getUser(), UserModelDTO.class);

            return "redirect:" + properties.getOAuthSuccessPage() + "?t=" + authenticationToken.getToken() + "&u="
                    + Base64JSONObjectEncoder.encode(userPublicData);

        } catch (Exception e) {
            return "redirect:" + properties.getOAuthErrorPage() + "?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }

    }

    /**
     * Receives a notification of logout to run events on the backend.
     *
     * @return Successful Response.
     */

    @PostMapping("/logout")
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> logout() {
        AuthenticationToken token;
        token = authenticationService.getTokenByUserId(ControllerUtils.getContextUserId());
        oauthService.logout(token);
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(new GenericSuccessResponse()));
    }

}

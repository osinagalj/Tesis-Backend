package com.unicen.core.security;

import com.unicen.core.model.AuthenticationToken;
import com.unicen.core.model.User;
import com.unicen.core.services.AuthenticationService;
import com.unicen.core.services.EntitiesDrawer;
import com.unicen.core.services.EnvironmentPropertiesService;
import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.unicen.AppBackendDevInitializer.getAuthenticationService;

public class AuthenticationServiceFilter extends CustomBearerTokenAuthenticationFilter {

    private final List<String> whitelistedPaths;
    private final AuthenticationService authenticationService;
    private static Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceFilter.class);


    private final String TOKEN_DEV = "abcd";
    private final List<String> BASIC_WHITELISTED_PATHS = List.of("/auth/login", "/auth/validate", "/auth/sign-up", "/auth/validate-forgot-password",
            "/auth/forgot-password", "/auth/validate-code", "/auth/login-with-code", "/auth/login-code", "/auth/login-method", "/status",
            "/oauth/connect/google", "/oauth/callback/google", "/images/upload", "/admin/public/.*", "/send-email", "images", "/image-result");

    public AuthenticationServiceFilter(AuthenticationService authenticationService, List<String> extraPaths) {
        this.authenticationService = authenticationService;
        this.whitelistedPaths = Stream.concat(BASIC_WHITELISTED_PATHS.stream(), extraPaths.stream()).collect(Collectors.toList());
    }

    public AuthenticationServiceFilter(AuthenticationService authenticationService) {
        this(authenticationService, new ArrayList<>());
    }

    @Override
    protected List<String> getWhitelistedPaths() {
        return Collections.unmodifiableList(this.whitelistedPaths);
    }


    @Autowired
    private GlobalApplicationContext globalApplicationContext; // to force Spring instantiation

    public  EnvironmentPropertiesService getEnviromentPropertiesService() {
        return (EnvironmentPropertiesService) GlobalApplicationContext.getBean("environmentPropertiesService");
    }

    @Override
    protected Authentication getAuthorizationFromToken(String token) {
        if(getEnviromentPropertiesService().isDevEnvironment() ){
            if(TOKEN_DEV.equals(token)){
                User userUser = EntitiesDrawer.getAdminUser();
                token = getAuthenticationService().loginUsingPassword(userUser.getEmail(), "password").getToken();
                LOGGER.info("session token for dev environment: {}", token);
            }
        }

        Optional<AuthenticationToken> maybeToken = Optional.ofNullable(
                this.authenticationService.getByToken(token)
        );
        // no token, no authentication returned
        if (maybeToken.isEmpty() || maybeToken.get().getUser().disabled()) {
            return null;
        }
        User user = maybeToken.get().getUser();
        return GenericAuthenticationToken.of(user);
    }

}
package com.unicen.core.security;


import com.unicen.core.model.ApiKey;
import com.unicen.core.model.AuthenticationToken;
import com.unicen.core.model.User;
import com.unicen.core.services.AuthenticationService;
import org.springframework.security.core.Authentication;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthenticationServiceFilter extends CustomBearerTokenAuthenticationFilter {

    private final List<String> whitelistedPaths;
    private final AuthenticationService authenticationService;

    private final List<String> BASIC_WHITELISTED_PATHS = List.of("/auth/login", "/auth/validate", "/auth/sign-up", "/auth/validate-forgot-password",
            "/auth/forgot-password", "/auth/validate-code", "/auth/login-with-code", "/auth/login-code", "/auth/login-method", "/status",
            "/oauth/connect/google", "/oauth/callback/google", "/images/upload", "/admin/public/.*", "/send-email");

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

    @Override
    protected Authentication getAuthorizationFromToken(String token) {
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

    @Override
    protected Authentication getAuthorizationFromApiKeyValue(String token) {
        Optional<ApiKey> maybeApiKey = Optional.ofNullable(
            this.authenticationService.getApiKey(token)
        );

        // no token, no authentication returned
        if (maybeApiKey.isEmpty()) {
            return null;
        }

        return GenericAuthenticationToken.of(maybeApiKey.get());
    }
}
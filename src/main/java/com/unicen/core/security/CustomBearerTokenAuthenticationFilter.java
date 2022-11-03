package com.unicen.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Generic class that filters requests by a Bearer token present on the request
 * <p>
 * TODO move to java-fox, improve documentation
 */
public abstract class CustomBearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String API_KEY_HEADER = "X-API-KEY";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        String apiKeyValue = request.getHeader(API_KEY_HEADER);
        allowCors(request, response);
        if (preflightRequest(request)) {
            response.setStatus(200);
            return;
        }
        if (getWhitelistedPaths().stream().anyMatch(uriPattern -> request.getRequestURI().matches(uriPattern))) {
            filterChain.doFilter(request, response);
            return;
        }
        // ensure token as a value
        Authentication auth = null;
        if (authorization != null) {
            String token = authorization.replaceAll("Bearer", "").trim();
            if(token.equals("abcd")){
                System.out.println("bug");
            }
            auth = this.getAuthorizationFromToken(token);
        }
        if (auth == null) {
            logger.warn("Invalid Authorization. Token: [" + authorization + "]. Api Key: [" + apiKeyValue + "].");
            response.setStatus(401);
        } else {
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        }
    }

    private boolean preflightRequest(HttpServletRequest request) {
        return request.getMethod().equals("OPTIONS");
    }

    private void allowCors(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
    }

    protected abstract List<String> getWhitelistedPaths();

    protected abstract Authentication getAuthorizationFromToken(String token);

}

package com.unicen.core.security;

import com.unicen.core.model.ApiKey;
import com.unicen.core.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GenericAuthenticationToken extends AbstractAuthenticationToken {

    private Long userId;
    private String username;

    public static GenericAuthenticationToken of(User user) {
        return new GenericAuthenticationToken(user.getId(), user.getEmail(), generateAuthorities(user));
    }

    public static GenericAuthenticationToken of(ApiKey apiKey) {
        return new GenericAuthenticationToken(apiKey.getIssuer().getId(), apiKey.getIssuer().getEmail(), generateAuthorities(apiKey));
    }

    public GenericAuthenticationToken(Long userId, String username) {
        this(userId, username, null);
    }

    public GenericAuthenticationToken(Long userId, String username, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.username = username;
        this.setAuthenticated(Objects.nonNull(userId) && StringUtils.isNotEmpty(username));
    }

    @Override
    public Object getCredentials() {
        return this.userId;
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }

    public String getUsername() {
        return username;
    }

    private static List<GrantedAuthority> generateAuthorities(User user) {
        return user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    private static List<GrantedAuthority> generateAuthorities(ApiKey apiKey) {
        return apiKey.getScopes().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

    public boolean hasAuthority(String... roles) {
        return this.getAuthorities().stream().anyMatch(a -> Arrays.asList(roles).contains(a.getAuthority()));
    }
}

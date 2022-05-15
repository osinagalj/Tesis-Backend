package com.unicen.app.services;

import com.unicen.app.model.AuthenticationToken;
import com.unicen.app.repositories.AuthenticationTokenRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationTokenService {
    private AuthenticationTokenRepository repository;

    public AuthenticationTokenService(AuthenticationTokenRepository repository) {
        this.repository = repository;
    }

    public Optional<AuthenticationToken> getTokenByUserId(Long userId) {
        return repository.getByUserId(userId);
    }

    public int deleteTokensExpiredBefore(Date date) {
        return this.repository.deleteTokensExpiredBefore(date);
    }

    /**
     * This method removes the token from the cache (does not perform any operations on the database)
     * @param token
     */
    @CacheEvict(value = "core_token")
    public void invalidateTokenFromCache(String token) {
        //Additional operation
    }
}

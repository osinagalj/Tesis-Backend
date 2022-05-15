package com.unicen.app.services;


import com.unicen.app.exceptions.CoreApiException;
import com.unicen.app.model.ApiKey;
import com.unicen.app.model.ApiScope;
import com.unicen.app.model.User;
import com.unicen.app.repositories.ApiKeyRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class ApiKeyService extends PublicObjectCrudService<ApiKey, ApiKeyRepository> {
    private final UserService userService;
    private final ApiScopeService apiScopeService;

    public ApiKeyService(ApiKeyRepository repository, UserService userService, ApiScopeService apiScopeService) {
        super(repository);
        this.userService = userService;
        this.apiScopeService = apiScopeService;
    }

    @Override
    protected void updateData(ApiKey existingObject, ApiKey updatedObject) {
        existingObject.setExpirationDate(updatedObject.getExpirationDate());
        existingObject.setScopes(updatedObject.getScopes());
        existingObject.setDisabledAt(updatedObject.getDisabledAt());
    }

    @Override
    protected Class<ApiKey> getObjectClass() {
        return ApiKey.class;
    }

    @Nullable
    public ApiKey getByKey(String key) {
        return this.repository.getByKeyValue(key).orElse(null);
    }

    public ApiKey getByKey(String key, Supplier<ApiKey> supplier) {
        return this.repository.getByKeyValue(key).orElseGet(supplier);
    }

    @Transactional
    public ApiKey issueKey(String issuerEmail, Collection<String> scopes) {
        User user = this.userService.getByEmail(issuerEmail);
        Set<ApiScope> scopeSet = this.apiScopeService.getByNames(scopes);

        return this.issueKey(user, scopeSet);
    }

    @Transactional
    public ApiKey issueKey(User issuer, Set<ApiScope> scopes) {
        return this.save(new ApiKey(issuer, freshApiKeyValue(), scopes));
    }

    public ApiKey revokeKeyValue(String keyValue) {
        ApiKey apiKey = this.getByKey(keyValue, () -> {
            throw CoreApiException.objectNotFound();
        });

        apiKey.setDisabledAt(new Date());
        this.save(apiKey);

        return apiKey;
    }

    public ApiKey validateKey(String key) {
        return this.repository.getByKeyValue(key).orElseThrow(() -> CoreApiException.validationError("Invalid api key"));
    }

    public void ensureScopes(String key, Collection<ApiScope> scopes) {
        ApiKey apiKey = this.validateKey(key);
        if (!apiKey.getScopes().containsAll(scopes)) {
            throw CoreApiException.insufficientPermissions();
        }
    }

    @Transactional
    public ApiKey removeScopesFromApiKey(String keyValue, Collection<String> scopes) {
        ApiKey apiKey = this.validateKey(keyValue);
        Set<ApiScope> scopeSet = this.apiScopeService.getByNames(scopes);

        apiKey.getScopes().removeAll(scopeSet);

        save(apiKey);

        return apiKey;
    }

    @Transactional
    public ApiKey addScopesToApiKey(String keyValue, Collection<String> scopes) {
        ApiKey apiKey = this.validateKey(keyValue);
        Set<ApiScope> scopeSet = this.apiScopeService.getByNames(scopes);

        apiKey.getScopes().addAll(scopeSet);

        save(apiKey);

        return apiKey;
    }

    /**
     * Returns a ApiKey matching the key value, creating it if it does not exist or returning an existing one if
     * it is registered already in the DB
     * @return the existing/created {@link ApiKey}
     */
    @Transactional
    public ApiKey ensure(String key, User issuer, Set<ApiScope> scopes) {
        return getByKey(key, () -> this.save(new ApiKey(issuer, key, scopes)));
    }

    private String freshApiKeyValue() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

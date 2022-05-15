package com.unicen.app.services;


import com.unicen.app.configuration.ApplicationPropertiesService;
import com.unicen.app.configuration.PasswordRulesConfiguration;
import com.unicen.app.exceptions.CoreApiException;
import com.unicen.app.exceptions.ObjectValidationFailed;
import com.unicen.app.model.*;
import com.unicen.app.repositories.AuthenticationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Service
public class AuthenticationService extends CrudService<AuthenticationToken, AuthenticationTokenRepository> implements ObservableService<AuthEvent, Object> {

    private final UserService userService;
    private final ValidationCodeService validationCodeService;
    private final ApiKeyService apiKeyService;
    private final ApplicationPropertiesService properties;
    private final List<Observer<AuthEvent, Object>> observers;

    private final PasswordRulesConfiguration passwordRulesConfiguration;

    public AuthenticationService(AuthenticationTokenRepository repository, UserService userService, ValidationCodeService validationCodeService,
                                 ApiKeyService apiKeyService, ApplicationPropertiesService properties,
                                 @Autowired(required = false) PasswordRulesConfiguration passwordRulesConfiguration) {
        super(repository);
        this.userService = userService;
        this.validationCodeService = validationCodeService;
        this.apiKeyService = apiKeyService;
        this.properties = properties;
        this.passwordRulesConfiguration = passwordRulesConfiguration != null ? passwordRulesConfiguration : new DefaultPasswordRulesConfiguration();
        this.observers = new ArrayList<>();
    }

    @Override
    protected Class<AuthenticationToken> getObjectClass() {
        return AuthenticationToken.class;
    }

    @Nullable
    @Cacheable("core_token")
    public AuthenticationToken getByToken(String token) {
        return this.repository.getByToken(token).orElse(null);
    }

    public ApiKey getApiKey(String key) {
        return this.apiKeyService.getByKey(key);
    }

    @Transactional
    private AuthenticationToken loginUsingPassword(String email, Function<User, Boolean> challenge) {
        User user = userService.getByEmail(email, () -> {
            throw CoreApiException.authenticationFailed(false);
        });

        if (user.disabled()) {
            throw new ObjectValidationFailed(user, "User is not enabled");
        }

        if (!challenge.apply(user)) {
            throw CoreApiException.authenticationFailed(true);
        }

        return renewToken(user);
    }

    @Transactional
    public AuthenticationToken loginUsingPassword(String email, String password) {
        return loginUsingPassword(email, (user) -> userService.match(password, user));
    }

    @Transactional
    public AuthenticationToken loginUsingCode(String email, String code) {
        return loginUsingPassword(email, (user) -> {
            AtomicBoolean codeValidationSucceeded = new AtomicBoolean(false);

            validationCodeService.useCode(code, user, () -> {
                codeValidationSucceeded.set(true);
            });

            return codeValidationSucceeded.get();
        });
    }

    @Override
    protected void updateData(AuthenticationToken existingObject, AuthenticationToken updatedObject) {
        // Authentication tokens should be immutable
    }


    @Transactional
    public void validateUserEmailWithAuthenticationCode(String email, String code) {
        User user = userService.getByEmail(email);
        validationCodeService.useCode(code, user, () -> userService.markAsEnabled(user.getEmail()));
    }

    @Transactional
    public void validateForgotPasswordWithAuthenticationCode(String email, String code, String password) {
        passwordRulesConfiguration.validatePassword(password);

        User user = userService.getByEmail(email);
        validationCodeService.useCode(code, user, () -> userService.updatePassword(user, password));
    }

    public User signUpWithPassword(String firstName, String lastName, String email, String password) {
        passwordRulesConfiguration.validatePassword(password);

        User freshUser = userService.registerWithPassword(firstName, lastName, email, password);
        notifyAll(AuthEvent.SIGN_UP, freshUser);

        return freshUser;
    }

    public void forgotPassword(String email) {
        User registeredUser = userService.getByEmail(email);

    }

    public AuthenticationToken renewToken(User user) {
        repository.invalidateOldTokensForUserId(user.getId());
        return save(new AuthenticationToken(user, freshToken()));
    }

    public AuthenticationToken ensureUserAndToken(String firstName, String lastName, String email) {
        return ensureUserAndTokenWithRole(firstName, lastName, email, EntitiesDrawer.userRole().getName());
    }

    public AuthenticationToken ensureUserAndTokenWithRole(String firstName, String lastName, String email, String roleName) {
        // TODO allow role to be specified
        User user = userService.ensureWithRole(firstName, lastName, email, roleName);
        return renewToken(user);
    }

    public void setLastLogin(User user) {
        user.setLastLogin(new Date());
        userService.save(user);
    }

    private String freshToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public AuthenticationToken getTokenByUserId(Long userId) {
        Optional<AuthenticationToken> maybeToken = repository.getByUserId(userId);
        if (maybeToken.isPresent()) {
            return maybeToken.get();
        } else {
            throw CoreApiException.validationError("[AUTH] Token not found for ID: " + userId, "The user does not have an associated token", true);
        }
    }


    @Override
    public void addObserver(Observer<AuthEvent, Object> observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer<AuthEvent, Object> observer) {
        observers.remove(observer);
    }

    @Override
    public List<Observer<AuthEvent, Object>> getObservers() {
        return Collections.unmodifiableList(observers);
    }

    private static class DefaultPasswordRulesConfiguration implements PasswordRulesConfiguration {

        @Override
        public void validatePassword(String password) {

        }
    }

    private String encodeText(String text) {
        return URLEncoder.encode(text, StandardCharsets.UTF_8);
    }

    @Transactional
    public void updatePassword(String email, String plainPassword) {
        passwordRulesConfiguration.validatePassword(plainPassword);

        User user = userService.getByEmail(email);
        String template = properties.getPasswordUpdateEmailTemplate();

        userService.updatePassword(user, plainPassword);
    }
}

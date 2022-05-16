package com.unicen.core.services;

import com.unicen.core.configuration.PasswordRulesConfiguration;
import com.unicen.core.exceptions.CoreApiException;
import com.unicen.core.model.*;
import com.unicen.core.repositories.AuthenticationTokenRepository;
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
public class AuthenticationService extends CrudService<AuthenticationToken, AuthenticationTokenRepository> {

    private final UserService userService;
    private final ValidationCodeService validationCodeService;
    private final ApplicationPropertiesService properties;

    private final EmailService emailService;

    private final PasswordRulesConfiguration passwordRulesConfiguration;

    public AuthenticationService(AuthenticationTokenRepository repository, UserService userService, ValidationCodeService validationCodeService,
                                 ApplicationPropertiesService properties, EmailService emailService, @Autowired(required = false) PasswordRulesConfiguration passwordRulesConfiguration) {
        super(repository);
        this.userService = userService;
        this.validationCodeService = validationCodeService;
        this.properties = properties;
        this.emailService = emailService;
        this.passwordRulesConfiguration = passwordRulesConfiguration != null ? passwordRulesConfiguration : new DefaultPasswordRulesConfiguration();

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

    @Transactional
    private AuthenticationToken loginUsingPassword(String email, Function<User, Boolean> challenge) {
        User user = userService.getByEmail(email, () -> {
            throw CoreApiException.authenticationFailed(false);
        });
        if (user.disabled()) {
            throw CoreApiException.validationError("User is not enabled");
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

    public AuthenticationToken signUpWithPassword(String firstName, String lastName, String email, String password) {
        passwordRulesConfiguration.validatePassword(password);
        User freshUser = userService.registerWithPassword(firstName, lastName, email, password);
        createValidationCode(freshUser, true);
        AuthenticationToken a = loginUsingPassword(email, (user) -> userService.match(password, user));
        return a;

    }

    @Transactional
    public ValidationCode createValidationCode(User user, boolean sendEmail) {
        ValidationCode code = validationCodeService.createForEmailValidation(user);
        if (sendEmail) {
            String template = properties.getValidationEmailTemplate();
            template = template.replace("${link}",
                    properties.getValidationBaseUrl() + "?code=" + code.getCode() + "&email=" + this.encodeText(user.getEmail()));
            emailService.sendEmail(properties.getFromEmail(), user.getEmail(), template);
        }
        return code;
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
        userService.updatePassword(user, plainPassword);
    }
}

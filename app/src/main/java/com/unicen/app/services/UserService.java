package com.unicen.app.services;


import com.unicen.app.exceptions.CoreApiException;
import com.unicen.app.exceptions.ObjectNotFoundException;
import com.unicen.app.exceptions.ValidationException;
import com.unicen.app.model.AccessRole;
import com.unicen.app.model.AuthenticationToken;
import com.unicen.app.model.User;
import com.unicen.app.repositories.UserRepository;
import com.unicen.app.security.UserPasswordEncoder;

import com.unicen.app.security.utils.RandomStringGenerator;
import org.slf4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;

@Service
public class UserService extends PublicObjectCrudService<User, UserRepository> {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserPasswordEncoder userPasswordEncoder;
    private final AccessRoleService accessRoleService;
    private final AuthenticationTokenService authenticationTokenService;

    /**
     * A secret that is generated once per app start and can be accessed only by the engineering team for doing admin-related
     * tasks like administering administrators
     */
    private String oneTimeAdminSecret;

    public UserService(UserRepository repository, UserPasswordEncoder encoder, @Lazy AccessRoleService accessRoleService, AuthenticationTokenService authenticationTokenService) {
        super(repository);
        this.userPasswordEncoder = encoder;
        this.accessRoleService = accessRoleService;
        this.authenticationTokenService = authenticationTokenService;
        this.oneTimeAdminSecret = generateOneTimeAdminSecret();
    }

    @Override
    protected void updateData(User user, User updateDTO) {
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setPhone(updateDTO.getPhone());
        user.setAvatarUrl(updateDTO.getAvatarUrl());
        user.setEmail(updateDTO.getEmail());
        user.setDisabledAt(updateDTO.getDisabledAt());
    }

    /**
     * Updates the password for a specific user
     * @param user the user which passwords is going to be updated
     * @param plainPassword the plain plainPassword to be updated
     */
    public void updatePassword(User user, String plainPassword) {
        String hashedPassword = userPasswordEncoder.encode(plainPassword);
        user.setHashedPassword(hashedPassword);
    }

    @Override
    protected Class<User> getObjectClass() {
        return User.class;
    }

    @Transactional
    public User register(String firstName, String lastName, String email) {
        return repository.save(new User(firstName, lastName, email, null));
    }

    @Transactional
    public User registerWithPassword(String firstName, String lastName, String email, String plainPassword) {
        if (plainPassword == null) {
            throw new ValidationException("Password not provided");
        }

        return repository.save(new User(firstName, lastName, email, userPasswordEncoder.encode(plainPassword)));
    }

    @Transactional
    public User create(String firstName, String lastName, String email) {
        return repository.save(new User(firstName, lastName, email, null));
    }

    @Transactional
    public User create(String firstName, String lastName, String email, boolean enableAfterCreation) {
        User user = create(firstName, lastName, email);
        user.setDisabledAt(enableAfterCreation ? null : new Date());
        return user;
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return getByEmail(email, () -> {
            throw new ObjectNotFoundException(User.class, "email", email);
        });
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email, Supplier<User> supplier) {
        return repository.findByEmail(email).orElseGet(supplier);
    }

    /**
     * Returns a User matching the email address, creating it if it does not exist (with a password) or returning an existing one if
     * it is registered already in the DB
     * @param firstName first name of the {@link User} to be created (if it does not exist)
     * @param lastName last name of the {@link User} to be created (if it does not exist)
     * @param email email of the {@link User} to be created / obtained
     * @param clearPassword password of the {@link User} to be created (if it does not exist)
     * @return the existing/created {@link User}
     */
    @Transactional
    public User ensure(String firstName, String lastName, String email, String clearPassword) {
        return getByEmail(email, () -> registerWithPassword(firstName, lastName, email, clearPassword));
    }

    /**
     * Returns a User matching the email address, creating it if it does not exist or returning an existing one if
     * it is registered already in the DB
     * @param firstName first name of the {@link User} to be created (if it does not exist)
     * @param lastName last name of the {@link User} to be created (if it does not exist)
     * @param email email of the {@link User} to be created / obtained
     * @return the existing/created {@link User}
     */
    @Transactional
    public User ensure(String firstName, String lastName, String email) {
        return getByEmail(email, () -> create(firstName, lastName, email, true));
    }

    /**
     * Returns a User matching the email address, creating it if it does not exist or returning an existing one if
     * it is registered already in the DB. When creating the user, it also attached a specific role
     * @param firstName first name of the {@link User} to be created (if it does not exist)
     * @param lastName last name of the {@link User} to be created (if it does not exist)
     * @param email email of the {@link User} to be created / obtained
     * @param roleName name of the role to be assigned to the user
     * @return the existing/created {@link User}
     */
    @Transactional
    public User ensureWithRole(String firstName, String lastName, String email, String roleName) {
        User user = ensure(firstName, lastName, email);
        AccessRole role = accessRoleService.ensure(roleName);

        user.addRole(role);
        save(user);

        return user;
    }

    @Transactional
    public User createAdmin(String firstName, String lastName, String email, String clearPassword) {
        User user = ensure(firstName, lastName, email, clearPassword);

        user.addRole(EntitiesDrawer.adminRole());
        user.setDisabledAt(null);
        save(user);

        return user;
    }

    @Transactional
    public User addRole(String email, String roleName) {
        User user = getByEmail(email);

        user.addRole(accessRoleService.getByName(roleName));
        save(user);

        this.invalidateAuthTokenEntryFromCache(user);

        return user;
    }

    @Transactional
    public User setEnabledStatus(String email, boolean enabled) {
        User user = getByEmail(email);

        user.setDisabledAt(enabled ? null : new Date());
        save(user);

        this.invalidateAuthTokenEntryFromCache(user);

        return user;
    }

    @Transactional
    public User removeRole(String email, String roleName) {
        User user = getByEmail(email);

        user.removeRole(accessRoleService.getByName(roleName));
        save(user);

        this.invalidateAuthTokenEntryFromCache(user);

        return user;
    }

    @Transactional
    public User removeAllRoles(String email) {
        return this.removeAllRoles(getByEmail(email));
    }

    @Transactional
    public User removeAllRoles(User user) {
        user.setRoles(new HashSet<>());
        save(user);

        this.invalidateAuthTokenEntryFromCache(user);

        return user;
    }

    public User markAsEnabled(String email) {
        User user = save(getByEmail(email).enable());

        this.invalidateAuthTokenEntryFromCache(user);

        return user;
    }

    public boolean match(String password, User user) {
        if (!userPasswordEncoder.matches(password, user.getHashedPassword())) {
            throw CoreApiException.authenticationFailed(false);
        }
        return true;
    }

    private String generateOneTimeAdminSecret() {
        String secret = new RandomStringGenerator(64).nextString();
        logger.info("OTAS: " + secret);
        return secret;
    }

    public void validateOneTimeAdminSecret(String secretToValidate) {
        if (!oneTimeAdminSecret.equals(secretToValidate)) {
            throw CoreApiException.authenticationFailed(true);
        }
    }

    @Transactional
    public void updateAvatar(User user, String imageUrl) {
        if (imageUrl != null && !imageUrl.isBlank()) {
            user.setAvatarUrl(imageUrl);
            this.save(user);
        }
    }

    public void invalidateAuthTokenEntryFromCache(User user) {
        Optional<AuthenticationToken> maybeUserToken = this.authenticationTokenService.getTokenByUserId(user.getId());
        maybeUserToken.ifPresent(authenticationToken -> this.authenticationTokenService.invalidateTokenFromCache(authenticationToken.getToken()));
    }
}

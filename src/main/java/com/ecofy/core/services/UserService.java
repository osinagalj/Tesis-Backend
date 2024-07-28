package com.ecofy.core.services;

import com.ecofy.app.dto.ProfileDTO;
import com.ecofy.app.model.Image;
import com.ecofy.app.service.ImageService;
import com.ecofy.core.exceptions.CoreApiException;
import com.ecofy.core.model.AccessRole;
import com.ecofy.core.model.AuthenticationToken;
import com.ecofy.core.model.User;
import com.ecofy.core.repositories.UserRepository;
import com.ecofy.core.security.UserPasswordEncoder;

import com.ecofy.core.security.utils.RandomStringGenerator;
import org.slf4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService extends PublicObjectCrudService<User, UserRepository> {

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserPasswordEncoder userPasswordEncoder;
    private final ImageService imageService;

    private final AccessRoleService accessRoleService;
    private final AuthenticationTokenService authenticationTokenService;

    /**
     * A secret that is generated once per app start and can be accessed only by the engineering team for doing admin-related
     * tasks like administering administrators
     */
    private String oneTimeAdminSecret;

    public UserService(UserRepository repository, UserPasswordEncoder encoder, ImageService imageService, @Lazy AccessRoleService accessRoleService, AuthenticationTokenService authenticationTokenService) {
        super(repository);
        this.userPasswordEncoder = encoder;
        this.imageService = imageService;
        this.accessRoleService = accessRoleService;
        this.authenticationTokenService = authenticationTokenService;
        this.oneTimeAdminSecret = generateOneTimeAdminSecret();
    }

/*
    @Transactional
    public Optional<User> findUserWithPicture(String userExternalId) throws IOException {
        return  repository.findByExternalId(userExternalId); //repository.findByExternalId(userExternalId);
    }
*/

    @Transactional
    public Optional<User> findByExternalIdAndFetchImageEagerly(String userExternalId) throws IOException {
        return  repository.findByExternalIdAndFetchImageEagerly(userExternalId);   //new ArrayList<>();
    }

    @Override
    protected void updateData(User user, User updateDTO) {
        user.setFirstName(updateDTO.getFirstName());
        user.setLastName(updateDTO.getLastName());
        user.setPhone(updateDTO.getPhone());
        user.setAvatarUrl(updateDTO.getAvatarUrl());
        user.setEmail(updateDTO.getEmail());
        user.setDisabledAt(updateDTO.getDisabledAt());
        user.setImage(updateDTO.getImage());
    }

    @Transactional
    public void updatePictureOfUser(String userExternalId, String type, MultipartFile file) throws IOException {
        //TODO change this
        Optional<User> maybeUser = repository.findByEmail("dev@gmail.com"); //repository.findByExternalId(userExternalId);
        User user = maybeUser.orElseThrow( () -> new IllegalStateException("User not found"));

        Image image = new Image("Image from test 2",1,11,"U",type,"D",user, file.getBytes(), user, new ArrayList<>());
        image.ensureExternalId();
        user.setImage(imageService.save(image));
        this.update(user.getId(), user);
        System.out.println("finish updating user pict..");
    }

    /**
     * Updates the password for a specific user
     *
     * @param user          the user which passwords is going to be updated
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
            throw CoreApiException.validationError("Password not provided");
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
            throw CoreApiException.objectNotFound("The email " + email + " does not exists.");
        });
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email, Supplier<User> supplier) {
        return repository.findByEmail(email).orElseGet(supplier);
    }

    /**
     * Returns a User matching the email address, creating it if it does not exist (with a password) or returning an existing one if
     * it is registered already in the DB
     *
     * @param firstName     first name of the {@link User} to be created (if it does not exist)
     * @param lastName      last name of the {@link User} to be created (if it does not exist)
     * @param email         email of the {@link User} to be created / obtained
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
     *
     * @param firstName first name of the {@link User} to be created (if it does not exist)
     * @param lastName  last name of the {@link User} to be created (if it does not exist)
     * @param email     email of the {@link User} to be created / obtained
     * @return the existing/created {@link User}
     */
    @Transactional
    public User ensure(String firstName, String lastName, String email) {
        return getByEmail(email, () -> create(firstName, lastName, email, true));
    }

    /**
     * Returns a User matching the email address, creating it if it does not exist or returning an existing one if
     * it is registered already in the DB. When creating the user, it also attached a specific role
     *
     * @param firstName first name of the {@link User} to be created (if it does not exist)
     * @param lastName  last name of the {@link User} to be created (if it does not exist)
     * @param email     email of the {@link User} to be created / obtained
     * @param roleName  name of the role to be assigned to the user
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
        LOGGER.info("Secret: " + secret);
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

    /**
     * Returns a User matching the email address, creating it if it does not exist or returning an existing one if
     * it is registered already in the DB. When creating the user, it also attached a specific role
     *

     * @return the existing/created {@link User}
     */
    private void updateUserData(User user, ProfileDTO profileDTO){
        if(profileDTO.getFirstName() != null){
            user.setFirstName(profileDTO.getFirstName());
        }
        if(profileDTO.getLastName() != null){
            user.setLastName(profileDTO.getLastName());
        }

        if(profileDTO.getPhone() != null){
            user.setPhone(profileDTO.getPhone());
        }

        if(profileDTO.getCountry() != null){
            user.setCountry(profileDTO.getCountry());
        }

        if(profileDTO.getEmail() != null){
            user.setLastName(profileDTO.getLastName()); // TODO check email
        }
        repository.save(user);
    }

    /**
     * Update the profile of a user. Checks that the new email doesn't exist.
     *
     * @param profileDTO first name of the {@link User} to be created (if it does not exist)
     * @return the existing/created {@link User}
     */
    @Transactional
    public void updateProfileData(ProfileDTO profileDTO, String userEmail) throws CoreApiException{
        if(!userEmail.equals(profileDTO.getEmail())){
            if(repository.findByEmail(profileDTO.getEmail()).isPresent()) {
                throw CoreApiException.objectAlreadyExists("User with email: " + profileDTO.getEmail() + " already exists.");
            }
        }

        Optional<User> maybeUser = repository.findByExternalId(profileDTO.getExternalId());
        if(maybeUser.isPresent()){
            updateUserData(maybeUser.get(), profileDTO);
        }else{
            throw CoreApiException.objectNotFound("User with external id: " + profileDTO.getExternalId() + " not found.");
        }
    }
}

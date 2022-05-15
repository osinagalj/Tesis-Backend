package com.unicen.app.controller;


import com.unicen.app.configuration.EnvironmentPropertiesService;
import com.unicen.app.dto.ApiResultDTO;
import com.unicen.app.dto.AuthenticationTokenDTO;
import com.unicen.app.dto.PublicAuthenticationToken;
import com.unicen.app.exceptions.CoreApiException;
import com.unicen.app.model.*;
import com.unicen.app.services.AuthenticationService;
import com.unicen.app.services.OAuthService;
import com.unicen.app.services.UserService;
import com.unicen.app.utils.JsonMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication controller for logging in or signing up to the Application
 */
@RestController
@RequestMapping(value = "/auth")
@PreAuthorize("permitAll()")
public class AuthController extends GenericController<AuthenticationToken, AuthenticationTokenDTO> {

    private final OAuthService oauthService;
    private final EnvironmentPropertiesService properties;
    private AuthenticationService authenticationService;
    @Autowired private UserService userService;

    public AuthController(AuthenticationService authenticationService, OAuthService oauthService,
            @Qualifier("environmentPropertiesService") EnvironmentPropertiesService properties) {
        this.authenticationService = authenticationService;
        this.oauthService = oauthService;
        this.properties = properties;
    }

    /**
     * Login a User if the password and email are correct. And Return the session token.
     *

     * @return token Authentication Session Token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResultDTO<PublicAuthenticationToken>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        AuthenticationToken token = authenticationService.loginUsingPassword(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(PublicAuthenticationToken.of(token)));
    }

    /**
     * Registers a new User and return and Successful response if the user was saved correctly. Triggers an email
     * validation code to tje users mailbox in order to activate the account.
     *
     * @param signUpRequestDTO {@link SignUpRequestDTO} containing: firstName, lastName, email and password
     * @return Successful response if the user was register correctly
     */
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResultDTO<String>> login(@RequestBody SignUpRequestDTO signUpRequestDTO) {
        authenticationService.signUpWithPassword(signUpRequestDTO.getFirstName(), signUpRequestDTO.getLastName(), signUpRequestDTO.getEmail(),
                signUpRequestDTO.getPassword());
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(("A validation code has been sent to user email")));
    }

    /**
    * Send an email to recover your user password.
    * Returns a message if email is sent.
    *
    * @param login User's email and pasword (needs only email).
    * @return Successful response if the user was register correctly
    */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResultDTO<String>> forgotPassword(@RequestBody LoginRequestDTO login) {
        authenticationService.forgotPassword(login.getEmail());
        return ResponseEntity.ok(ApiResultDTO.ofSuccess("Sent you an email to recover your password"));
    }

    /**
     * Returns the logging method that the User of the given email has to use to access the App.
     *
     * @param email User's email
     * @return Response with body containing: loginUrl, label and helper values.
     */
    @GetMapping("/login-method")
    public ResponseEntity<ApiResultDTO<String>> getLoginMethod(@RequestParam() String email) {
        // TODO validate if the user email exists
        User user = userService.getByEmail(email);

        String loginUrl = "/login";
        String label = "Password";
        String helper = "Enter your password";

        return ResponseEntity.ok(ApiResultDTO.ofSuccess(new JsonMap("loginUrl", loginUrl).addProp("label", label).addProp("helper", helper).asJson()));
    }



    /**
     * Receives an email and an accessKey (maybe a password or an access code) and login the User into the App.
     * Returns the Session token.
     *
     * @param dto email and accessKey key-value pairs.
     * @return Successful Response with session token on its body
     */
    @PostMapping("/login-with-code")
    public ResponseEntity<ApiResultDTO<Object>> loginWithCode(@RequestBody Map<String, String> dto) {
        AuthenticationToken token;
        try {
            token = authenticationService.loginUsingCode(dto.get("email"), dto.get("code"));
        } catch (Exception e) {
            throw CoreApiException.insufficientPermissions("Can't validate user token");
        }

        return ResponseEntity.ok(ApiResultDTO.ofSuccess(modelToDto(AuthenticationTokenDTO.class, token)));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> validateAccount(@RequestParam(value = "code") String code,
            @RequestParam(value = "email") String email) {
        authenticationService.validateUserEmailWithAuthenticationCode(email, code);
        return ok();
    }

    /**
    * Receives an email, validation code and new password to modify your own password.
    * Returns a Message if password change sucessfully.
    *
    * @param changePassword email, validationCode and password key-value pairs.
    * @return Successful Response.
    */
    @PostMapping("/validate-forgot-password")
    public ResponseEntity<ApiResultDTO<String>> validateForgotPassword(@RequestBody ChangePasswordDTO changePassword) {
        authenticationService.validateForgotPasswordWithAuthenticationCode(changePassword.getEmail(), changePassword.getValidationCode(),
                changePassword.getPassword());
        return ResponseEntity.ok(ApiResultDTO.ofSuccess("helper", "Password change sucessfully"));
    }

    @Override
    protected Class<AuthenticationTokenDTO> getDTOClass() {
        return AuthenticationTokenDTO.class;
    }

    @Override
    protected Class<AuthenticationToken> getObjectClass() {
        return AuthenticationToken.class;
    }
}
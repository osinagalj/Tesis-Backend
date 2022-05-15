package com.unicen.app.configuration;


import com.unicen.app.model.AuthenticationToken;

public interface OAuthFlowAdapter {

    /**
     * Defines if a user is allowed to login/register or not via the OAuth 2.0 flow. If returns true, then
     * the process will continue, otherwise the error page will be returned and no token will be generated
     *
     * @param email email of the user
     * @return true
     */
    boolean acceptLogin(String email);

    /**
     * Function called after the OAuth 2.0 flow is completed successfully, including the
     * fresh authentication token created as a result of the flow execution. Note that the {@link AuthenticationToken}
     * returned also includes the `user` that owns the token, so anything that need to be done with a user - for instance
     * creating new roles for it, decorating it somehow, etc. - can be done by overriding this method
     *
     * @param token the token created for the user as a result of the successful execution of the OAuth 2.0 flow
     */
    void afterLogin(AuthenticationToken token);

    /**
     * Function called before a logout event is loaded on the DB. This token includes the user, so any operation that
     * requires the user can be ran overriding this method.
     *
     * @param token the token created for the user as a result of the successful execution of the OAuth 2.0 flow
     */
    void beforeLogout(AuthenticationToken token);

}

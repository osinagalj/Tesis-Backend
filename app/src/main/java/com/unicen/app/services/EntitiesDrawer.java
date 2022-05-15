package com.unicen.app.services;

import com.unicen.app.fox.spring.lightweightcontainer.GlobalApplicationContext;
import com.unicen.app.model.AccessRole;
import com.unicen.app.model.ApiKey;
import com.unicen.app.model.ApiScope;
import com.unicen.app.model.User;

import java.util.Date;
import java.util.Random;
import java.util.Set;

public class EntitiesDrawer {

    public static final String DEFAULT_PASSWORD = "password";
    static private Random rnd = new Random(new Date().getTime());

    public static String randomMail() {
        return "newTest." + randomInt() + "@ensolvers.com";
    }

    public static int randomInt() {
        return rnd.nextInt();
    }

    public static String randomCode() {
        return "" + randomInt();
    }

    /**
     * Returns a role for a user that should be able to do critical administrative tasks - like managing admins
     * This role should be used only for engineers to do tasks that require a very high clearance level
     * @return the role
     */
    public static AccessRole superAdminRole() {
        return newRole("ROLE_SUPERADMIN");
    }

    /**
     * Returns a role for a user that should be able to perform admin tasks (like managing users for instance)
     * in addition to actions that a regular user is able to do
     * @return the role
     */
    public static AccessRole adminRole() {
        return ensureRole("ROLE_ADMIN");
    }

    /**
     * Returns a role that should have support permissions (like chatting with other users to solve problems)
     * @return the role
     */
    public static AccessRole supportRole() {
        return ensureRole("ROLE_SUPPORT");
    }

    /**
     * Returns a role that should have all the "regular" permissions to use the app
     * @return the role
     */
    public static AccessRole userRole() {
        return ensureRole("ROLE_USER");
    }

    /**
     * Returns a scope for a user that should be able to perform admin tasks (like managing users for instance)
     * @return the scope
     */
    public static ApiScope adminScope() {
        return ensureScope("SCOPE_ADMIN");
    }

    public static User userUser() {
        User user = newUser().enable();
        user.getRoles().clear();
        user.getRoles().add(userRole());
        return getUserService().save(user);
    }

    public static User adminUser() {
        User user = newUser("Sample", "User", "dev@ensolvers.com", DEFAULT_PASSWORD);
        user.getRoles().clear();
        user.getRoles().add(adminRole());
        return getUserService().save(user);
    }

    public static User adminUserWithPassword(String password) {
        User user = newUser("Sample", "User", "dev@ensolvers.com", password);
        user.getRoles().clear();
        user.getRoles().add(adminRole());
        return getUserService().save(user);
    }

    public static User newAdminUser() {
        User user = newUser();
        user.getRoles().clear();
        user.getRoles().add(adminRole());
        return getUserService().save(user);
    }

    public static User superAdminUser() {
        User user = newUser("Omniscient", "User", "super@ensolvers.com", DEFAULT_PASSWORD);
        user.getRoles().clear();
        user.getRoles().add(superAdminRole());
        return getUserService().save(user);
    }

    public static User supportUser() {
        User user = newUser("Support", "User", "support@ensolvers.com", DEFAULT_PASSWORD);
        user.getRoles().clear();
        user.getRoles().add(supportRole());
        return getUserService().save(user);
    }

    public static User newUser() {
        return getUserService().ensure("First test name", "Last test name", randomMail(),
            DEFAULT_PASSWORD);
    }

    public static User newEnabledUser() {
        return getUserService().save(newUser().enable());
    }

    public static User drJekill() {
        return newUser("Henry", "Jekyll", "henry.j@test.com", DEFAULT_PASSWORD);
    }

    public static User drHouse() {
        return newUser("Gregory", "House", "gregory.h@test.com", DEFAULT_PASSWORD);
    }

    public static User drWho() {
        return newUser("Basil", "Who", "basil.w@test.com", DEFAULT_PASSWORD);
    }

    public static User drLecter() {
        return newUser("Hannibal", "Lecter", "hannibal.l@test.com", DEFAULT_PASSWORD);
    }

    public static User drGeller() {
        return newUser("Ross", "Geller", "ross.g@test.com", DEFAULT_PASSWORD);
    }



    public static User newUser(String name, String lastName, String email, String pass) {

        return getUserService().save(getUserService().ensure(name, lastName, email, pass).enable());
    }

    public static ApiKey newApiKey(User issuer, Set<ApiScope> apiScope) {
        return getApiKeyService().save(getApiKeyService().ensure("ADMIN_API_KEY", issuer, apiScope));
    }

    /**
     * Returns a new generic role identified by the name
     * @param name
     * @return
     *
     * @deprecated since the name is not semantically correct, this method does not creates a new role
     *             but returns a new one instead. Use {@code ensureRole} instead
     */
    @Deprecated()
    public static AccessRole newRole(String name) {
        return getRoleService().ensure(name);
    }

    public static AccessRole ensureRole(String name) {
        return getRoleService().ensure(name);
    }

    public static ApiScope ensureScope(String name) {
        return getApiScopeService().ensure(name);
    }



    public static ApiKey newAdminApiKey(User issuer) {
        return newApiKey(issuer, Set.of(adminScope()));
    }


    public static UserService getUserService() {
        return (UserService) GlobalApplicationContext.getBean("userService");
    }

    public static AccessRoleService getRoleService() {
        return (AccessRoleService) GlobalApplicationContext.getBean("accessRoleService");
    }

    public static ApiScopeService getApiScopeService() {
        return (ApiScopeService) GlobalApplicationContext.getBean("apiScopeService");
    }

    public static ApiKeyService getApiKeyService() {
        return (ApiKeyService) GlobalApplicationContext.getBean("apiKeyService");
    }

    public static LogEventService getEventService() {
        return (LogEventService) GlobalApplicationContext.getBean("logEventService");
    }

    public static DynamicPropertyService getDynamicPropertyService() {
        return (DynamicPropertyService) GlobalApplicationContext.getBean("dynamicPropertyService");
    }

}
package com.unicen.core.services;

import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import com.unicen.core.model.AccessRole;
import com.unicen.core.model.User;

public class EntitiesDrawer {

    public static final String DEFAULT_PASSWORD = "password";
    /**
     * Returns a role for a user that should be able to perform admin tasks (like managing users for instance)
     * in addition to actions that a regular user is able to do
     * @return the role
     */
    public static AccessRole adminRole() {
        return ensureRole("ROLE_ADMIN");
    }


    /**
     * Returns a role that should have all the "regular" permissions to use the app
     * @return the role
     */
    public static AccessRole userRole() {
        return ensureRole("ROLE_USER");
    }


    public static User userUser() {
        User user = newUser("John", "Johnson", "user@gmail.com", DEFAULT_PASSWORD);
        user.getRoles().clear();
        user.getRoles().add(userRole());
        return getUserService().save(user);
    }

    public static User adminUser() {
        User user = newUser("Sample", "User", "dev@gmail.com", DEFAULT_PASSWORD);
        user.getRoles().clear();
        user.getRoles().add(adminRole());
        return getUserService().save(user);
    }

    public static User getAdminUser(){
        return getUserService().getByEmail("dev@gmail.com");
    }

    public static User newUser(String name, String lastName, String email, String pass) {
        return getUserService().save(getUserService().ensure(name, lastName, email, pass).enable());
    }

    public static AccessRole ensureRole(String name) {
        return getRoleService().ensure(name);
    }

    public static UserService getUserService() {
        return (UserService) GlobalApplicationContext.getBean("userService");
    }

    public static AccessRoleService getRoleService() {
        return (AccessRoleService) GlobalApplicationContext.getBean("accessRoleService");
    }

}
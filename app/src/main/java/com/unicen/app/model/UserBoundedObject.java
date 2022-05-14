package com.unicen.app.model;

/**
 * Represents an object that is bounded to a {@code User}
 */
public interface UserBoundedObject {

    User getUser();

    void setUser(User user);

}

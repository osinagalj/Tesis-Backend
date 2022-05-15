package com.unicen.app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthAuthenticationInfo {

    private String email;
    private String token;
    private String firstName;
    private String lastName;

    public OAuthAuthenticationInfo(String firstName, String lastName, String email, String token) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.token = token;
    }

}

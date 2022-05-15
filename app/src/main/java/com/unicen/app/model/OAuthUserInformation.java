package com.unicen.app.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthUserInformation {

    private String email;
    private String firstName;
    private String lastName;

    public OAuthUserInformation(String email) {
        this.email = email;
    }

    public OAuthUserInformation(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

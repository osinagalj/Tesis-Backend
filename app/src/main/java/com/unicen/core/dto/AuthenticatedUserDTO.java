package com.unicen.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains the basic information of an authenticated user to be transferred
 * internally
 */
@Getter
@Setter
public class AuthenticatedUserDTO {

    private String email;

    public AuthenticatedUserDTO(String email) {
        this.email = email;
    }

}

package com.unicen.app.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic DTO class for User
 */
@Getter
@Setter
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String externalId;
    private String phone;
    private String avatarUrl;
}
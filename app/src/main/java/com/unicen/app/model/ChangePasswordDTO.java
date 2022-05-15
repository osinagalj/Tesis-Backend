package com.unicen.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic DTO class for PasswordChange
 */
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordDTO implements DtoAble<ChangePasswordDTO> {

    private String email;
    private String validationCode;
    private String password;
}

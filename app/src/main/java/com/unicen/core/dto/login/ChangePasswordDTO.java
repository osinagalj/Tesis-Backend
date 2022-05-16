package com.unicen.core.dto.login;

import com.unicen.core.model.DtoAble;
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

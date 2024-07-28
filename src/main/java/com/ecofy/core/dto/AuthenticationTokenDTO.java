package com.ecofy.core.dto;

import com.ecofy.core.model.DtoAble;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AuthenticationTokenDTO implements DtoAble<AuthenticationTokenDTO> {

    private String token;
    private UserDTO user;
    private Date expiresAt;

    public AuthenticationTokenDTO() {
    }
}

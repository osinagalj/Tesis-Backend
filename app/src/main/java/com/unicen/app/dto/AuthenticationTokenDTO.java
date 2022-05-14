package com.unicen.app.dto;

import com.unicen.app.model.DtoAble;
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

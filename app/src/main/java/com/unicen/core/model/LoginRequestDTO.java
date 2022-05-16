package com.unicen.core.model;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotNull
    String email;
    @NotNull
    String password;

}

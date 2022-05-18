package com.unicen.core.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDTO {
    String firstName;
    String lastName;
    String phone;
    String password;
    String passwordConfirmation;
    String email;
}

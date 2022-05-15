package com.unicen.core.model;

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

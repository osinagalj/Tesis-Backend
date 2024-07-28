package com.ecofy.core.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddRoleRequestDTO {

    private String email;
    private String roleName;

}

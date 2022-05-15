package com.unicen.core.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequestDTO {

    private String email;
    private boolean enabled;

}

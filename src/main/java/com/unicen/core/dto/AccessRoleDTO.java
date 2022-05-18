package com.unicen.core.dto;

import com.unicen.core.model.DtoAble;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic DTO class for Role User
 */
@Getter
@Setter
@NoArgsConstructor
public class AccessRoleDTO implements DtoAble<AccessRoleDTO> {
    private String externalId;
    private String name;
}
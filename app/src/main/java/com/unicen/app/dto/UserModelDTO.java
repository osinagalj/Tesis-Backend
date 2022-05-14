package com.unicen.app.dto;


import com.unicen.app.model.DTO;
import com.unicen.app.model.DtoAble;
import com.unicen.app.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Generic DTO class for User
 */
@Getter
@Setter
public class UserModelDTO implements DtoAble<UserModelDTO>, DTO<User> {
    private String firstName;
    private String lastName;
    private String email;
    private String externalId;
    private String phone;
    private String avatarUrl;
    private Boolean enabled;
    private Set<AccessRoleDTO> roles = new HashSet<>();

    @Override
    public UserModelDTO initWith(Object model) {
        UserModelDTO userModelDTO = DtoAble.super.initWith(model);
        userModelDTO.setEnabled(((User) model).enabled());
        return userModelDTO;
    }

    @Override
    public <T> T as(Class<T> clazz) {
        User user = (User) DtoAble.super.as(clazz);

        user.enable(getEnabled());
        user.ensureExternalId();

        return clazz.cast(user);
    }

    @Override
    public void postConstructFrom(User user) {
        setEnabled(user.enabled());
    }

    @Override
    public void afterGenerating(User user) {
        user.enable(getEnabled());
    }
}
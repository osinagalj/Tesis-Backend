package com.unicen.app.dto;

import com.unicen.app.model.DTO;
import com.unicen.app.model.DtoAble;
import com.unicen.app.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PublicUserModelDTO implements DtoAble<PublicUserModelDTO>, DTO<User> {
    private String firstName;
    private String lastName;
    private String externalId;
    private String avatarUrl;
    private Boolean enabled;

    public String getLastName() {
        return String.format("%s.", this.lastName.charAt(0));
    }

    @Override
    public PublicUserModelDTO initWith(Object model) {
        var dto = DtoAble.super.initWith(model);

        dto.setEnabled(((User) model).enabled());

        return dto;
    }

    @Override
    public <T> T as(Class<T> clazz) {
        var user = (User) DtoAble.super.as(clazz);

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
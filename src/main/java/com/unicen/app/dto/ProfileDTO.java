package com.unicen.app.dto;

import com.unicen.core.dto.UserModelDTO;
import com.unicen.core.model.DtoAble;
import com.unicen.core.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO implements DtoAble<ProfileDTO> {
    private String externalId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String country;

    @Override
    public ProfileDTO initWith(Object model) {
        User user = (User) model;
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhone(user.getPhone());
        profileDTO.setFirstName(user.getFirstName());
        profileDTO.setLastName(user.getLastName());
        return profileDTO;
    }
}
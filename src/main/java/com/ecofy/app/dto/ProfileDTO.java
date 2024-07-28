package com.ecofy.app.dto;

import com.ecofy.core.model.DtoAble;
import com.ecofy.core.model.User;
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
        profileDTO.setCountry(user.getCountry());
        return profileDTO;
    }
}
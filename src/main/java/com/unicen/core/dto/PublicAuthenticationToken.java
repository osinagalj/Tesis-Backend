package com.unicen.core.dto;

import com.unicen.core.model.AuthenticationToken;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.Date;

/**
 * Represents a token that can be shared without compromising ids or any other
 * user personal information
 */
@Getter
@Setter
public class PublicAuthenticationToken {

    private String token;
    private String userExternalId;
    private Date expiresAt;
    private UserModelDTO user;

    public static PublicAuthenticationToken of(AuthenticationToken authenticationToken) {
        PublicAuthenticationToken token = new PublicAuthenticationToken();
        token.setToken(authenticationToken.getToken());
        token.setUserExternalId(authenticationToken.getUser().getExternalId());
        token.setExpiresAt(authenticationToken.getExpiresAt());

        ModelMapper mm = new ModelMapper();
        UserModelDTO dto = mm.map(authenticationToken.getUser(), UserModelDTO.class);
        token.setUser(dto);
        return token;
    }
}

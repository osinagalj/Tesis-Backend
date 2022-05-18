package com.unicen.core.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "core_auth_token")
public class AuthenticationToken extends AuditableModel<AuthenticationToken> {
    private static final long ExpirationTime = 24 * 60 * 60 * 1000;

    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private String token;

    @Column(nullable = false)
    private Date expiresAt;

    public AuthenticationToken() {
    }

    public AuthenticationToken(User user, String token) {
        this.user = user;
        this.token = token;
        expiresAt = expirationTime();
    }

    private static Date expirationTime() {
        return new Date(new Date().getTime() + ExpirationTime);
    }
}

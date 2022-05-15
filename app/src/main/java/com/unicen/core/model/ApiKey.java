package com.unicen.core.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "core_api_key")
public class ApiKey extends AuditableModel<ApiKey> {
    // One year
    private static final long ExpirationTime = 365L * 24 * 60 * 60 * 1000;

    @JoinColumn(name = "issuer_id", referencedColumnName = "id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User issuer;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "core_api_key_scopes", inverseJoinColumns = @JoinColumn(name = "api_scope_id", referencedColumnName = "id"))
    @BatchSize(size = 20)
    private Set<ApiScope> scopes = new HashSet<>();

    private String keyValue;
    private Date expirationDate;


    public ApiKey() {
    }

    public ApiKey(User issuer, String keyValue) {
        this.issuer = issuer;
        this.keyValue = keyValue;
        this.expirationDate = expirationTime();
    }

    public ApiKey(User issuer, String keyValue, Collection<ApiScope> scopes) {
        this(issuer, keyValue);
        this.scopes = new HashSet<>(scopes);
    }

    private static Date expirationTime() {
        return new Date(new Date().getTime() + ExpirationTime);
    }
}
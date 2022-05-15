package com.unicen.core.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user in the platform
 */
@Entity
@Getter
@Setter
@Table(name = "core_user")
public class User extends AuditableModel<User> {
    private String firstName;
    private String lastName;
    @Column(unique = true) private String email;
    private String hashedPassword = "";
    private String phone;
    private String avatarUrl;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "core_user_access_roles", inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @BatchSize(size = 20) private Set<AccessRole> roles = new HashSet<>();
    private Date lastLogin;

    public User() {
        disable();
    }

    public User(String firstName, String lastName, String email, String hashedPassword) {
        this(firstName, lastName, email, hashedPassword, generateExternalId());
    }

    public User(String firstName, String lastName, String email, String hashedPassword, String externalId) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.externalId = externalId;
    }

    public String fullName() {
        return getFirstName() + " " + getLastName();
    }

    public String toString() {
        return "User{" + "email='" + email + '\'' + ", externalId='" + externalId + '\'' + '}';
    }

    public void addRole(AccessRole role) {
        roles.add(role);
    }

    public void removeRole(AccessRole role) {
        roles.remove(role);
    }
}
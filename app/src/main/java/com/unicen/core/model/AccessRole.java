package com.unicen.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Represents a role user in the platform
 */
@Entity
@Getter
@Setter
@Table(name = "core_access_role")
public class AccessRole extends AuditableModel<AccessRole> {
    private String name;

    public AccessRole() {
    }

    public AccessRole(String name, String externalId) {
        this.name = name;
        this.externalId = externalId;
    }

    public AccessRole(String name) {
        this(name, generateExternalId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        AccessRole that = (AccessRole) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
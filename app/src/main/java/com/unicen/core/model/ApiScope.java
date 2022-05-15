package com.unicen.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Represents an api scope in the platform
 */
@Entity
@Getter
@Setter
@Table(name = "core_api_scope")
public class ApiScope extends AuditableModel<ApiScope> {
    private String name;

    public ApiScope() {
    }

    public ApiScope(String name, String externalId) {
        this.name = name;
        this.externalId = externalId;
    }

    public ApiScope(String name) {
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
        ApiScope that = (ApiScope) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
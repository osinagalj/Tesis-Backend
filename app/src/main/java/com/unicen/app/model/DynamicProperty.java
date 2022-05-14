package com.unicen.app.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "core_dynamic_property")
public class DynamicProperty extends GenericModel {

    @Column(name = "prop_key") private String key;
    private String content;
    private Long sinceVersion;
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    public DynamicProperty() {
    }

    /**
     * Generates a Dynamic Property instance
     *
     * @param key          Unique Property key
     * @param content      Dynamic property content/value
     * @param sinceVersion Minimum version since this key will be available
     */
    public DynamicProperty(String key, String content, Long sinceVersion) {
        this.key = key;
        this.content = content;
        this.sinceVersion = sinceVersion;
    }

    protected boolean same(Object o) {
        return key.equals(((DynamicProperty) o).key);
    }

    public String toString() {
        return "DynamicProperty{" + "key='" + key + '\'' + ", content='" + content + '\'' + ", sinceVersion=" + sinceVersion + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + '}';
    }
}
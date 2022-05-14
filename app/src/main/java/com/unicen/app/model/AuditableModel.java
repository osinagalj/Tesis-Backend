package com.unicen.app.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
public class AuditableModel<T> extends PublicModel {

    @CreationTimestamp
    protected Timestamp createdAt;
    @UpdateTimestamp
    protected Timestamp updatedAt;
    private Date disabledAt;

    public boolean enabled() {
        return getDisabledAt() == null;
    }

    public boolean disabled() {
        return !enabled();
    }

    public T enable(boolean bool) {
        if (bool) {
            enable();
        } else {
            disable();
        }
        return (T) this;
    }

    public T enable() {
        setDisabledAt(null);
        return (T) this;
    }

    public T disable() {
        setDisabledAt(new Date());
        return (T) this;
    }

    public boolean toggleEnabled() {
        enable(!enabled());
        return enabled();
    }

    // Accessors
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDisabledAt() {
        return disabledAt;
    }

    public void setDisabledAt(Date disabledAt) {
        this.disabledAt = disabledAt;
    }
}

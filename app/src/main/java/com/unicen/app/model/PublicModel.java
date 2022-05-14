package com.unicen.app.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@MappedSuperclass
public class PublicModel extends GenericModel {

    @Column(unique = true) protected String externalId;

    public static String generateExternalId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public void ensureExternalId() {
        setExternalId(externalId == null ? generateExternalId() : externalId);
    }

    // Accessors
    public void setExternalId(String anId) {
        externalId = anId;
    };

    public String getExternalId() {
        return externalId;
    };

}
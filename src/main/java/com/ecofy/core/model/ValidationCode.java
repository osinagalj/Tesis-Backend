package com.ecofy.core.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;

/**
 * Represents a validation code that can be used to validate access information
 * like email, phone number, etc.
 */
@Getter
@Setter
@Entity
@Table(name = "core_validation_code")
public class ValidationCode extends GenericModel {
    private ValidationType type;
    private String validationInformation; // email address, phone number, etc
    private String code; // validation code
    private Date expirationDate;
    private Date usedAt;
    @JoinColumn(referencedColumnName = "id")
    @ManyToOne private User user; // a validation code might be issued for a particular user (or not)

    static private Date expirationTime() {
        Calendar currentTimeNow = Calendar.getInstance();
        currentTimeNow.add(5, Calendar.MINUTE);
        return currentTimeNow.getTime();
    }

    public ValidationCode() {
    }

    public ValidationCode(ValidationType type, String validationInformation, String code) {
        this.type = type;
        this.code = code;
        this.validationInformation = validationInformation;
        expirationDate = expirationTime();
    }

    public ValidationCode(ValidationType type, String validationInformation, String code, User user) {
        this(type, validationInformation, code);
        this.user = user;
    }

    public ValidationCode expire() {
        setUsedAt(new Date());
        return this;
    }

    public boolean unused() {
        return getUsedAt() != null;
    }

    public boolean unexpired() {
        return !getExpirationDate().after(new Date());
    }


}

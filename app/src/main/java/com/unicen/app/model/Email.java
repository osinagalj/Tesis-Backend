package com.unicen.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Representation of an Email
 *
 * @author Sebastian Javier Guzman
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "core_email")
public class Email extends AuditableModel<Email> {
    @Column(name = "sender") private String from;
    @Column(name = "recipients") private String to;
    private String subject;
    private String body;
    private Boolean isHtml;
    private String awsId;

    public Email(String sender, String subject, String body, Boolean isHtml, String... recipients) {
        from = sender;
        to = String.join(",", recipients);
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
        ensureExternalId();
    }

    public boolean wasSent() {
        return awsId != null;
    }
}
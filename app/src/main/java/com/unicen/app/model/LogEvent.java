package com.unicen.app.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "core_log_event")
public class LogEvent extends AuditableModel<LogEvent> implements UserBoundedObject {
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne private User user;
    private EventType type;
    private String content;

    public LogEvent() {
    }

    public LogEvent(User user, EventType type, String content) {
        this(user, type, content, generateExternalId());
    }

    public LogEvent(User user, EventType type, String content, String externalId) {
        this.user = user;
        this.type = type;
        this.content = content;
        this.externalId = externalId;
    }

}
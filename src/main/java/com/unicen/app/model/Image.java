package com.unicen.app.model;

import com.unicen.core.model.AuditableModel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import javax.persistence.*;

/**
 * Represents a user in the platform
 */
@Entity
@Getter
@Setter
@Table(name = "image")
public class Image extends AuditableModel<Image> {
    @Column(name = "width")
    private int width;
    @Column(name = "height")
    private int height;
    @NonNull
    @Column(name = "url")
    private String url;
    @Column(name = "type")
    private String type;
    @Column(name = "description")
    private String description;
}
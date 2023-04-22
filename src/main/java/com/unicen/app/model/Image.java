package com.unicen.app.model;

import com.unicen.core.model.AuditableModel;
import com.unicen.core.model.User;
import lombok.*;

import javax.persistence.*;

/**
 * Represents a user in the platform
 */
@Entity
@Getter
@Setter
@Table(name = "image")
@AllArgsConstructor
@NoArgsConstructor
public class Image extends AuditableModel<Image> {
    @Column(name = "name")
    private String name;
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

    @OneToOne(mappedBy = "image", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;



   /* @Transient*/
    @Lob
    @Column(name = "image_data", length = 1000)
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

}
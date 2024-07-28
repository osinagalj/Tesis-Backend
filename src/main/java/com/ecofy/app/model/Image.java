package com.ecofy.app.model;

import com.ecofy.core.model.AuditableModel;
import com.ecofy.core.model.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

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
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name = "image_data", length = 1000)
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private List<ImageResult> imagesList;

}
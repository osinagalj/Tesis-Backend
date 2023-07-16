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
@Table(name = "image_result")
@AllArgsConstructor
@NoArgsConstructor
public class ImageResult extends AuditableModel<ImageResult> {
    @Column(name = "name")
    private String name;

    @Column(name = "algorithm")
    private String algorithm;

    @Column(name = "ratio")
    private Integer ratio;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "image_data", length = 1000)
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_image_id")
    private Image originalImageId;

}
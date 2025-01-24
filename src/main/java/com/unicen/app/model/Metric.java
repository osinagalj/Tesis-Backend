package com.unicen.app.model;

import com.unicen.core.model.AuditableModel;
import com.unicen.core.model.PublicModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Table(name = "metric")
public class Metric extends AuditableModel<Image> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_image_id")
    private Image originalImageId;

    @Column(name = "ratio")
    private Integer ratio;

    // Relación con Indicator ENL
    @OneToOne
    @JoinColumn(name = "enl_indicator_id", nullable = true) // Columna para ENL
    private Indicator ENL;

    // Relación con Indicator SSI
    @OneToOne
    @JoinColumn(name = "ssi_indicator_id", nullable = true) // Columna para SSI
    private Indicator SSI;

//    Indicator ENL;
//    Indicator SSI;
//    Indicator SMPI;
//    Indicator SSIM;
//    Indicator SRS;
//    Indicator MACANA;
//    Indicator ENTROPY;
//    Indicator LUMINANCE;
//    Indicator CONTRAST;
//    Indicator STRUCTURE;

}

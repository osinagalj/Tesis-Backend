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
public class Metric extends AuditableModel<Metric> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_image_id")
    private Image originalImageId;

    @Column(name = "ratio")
    private Integer ratio;


    // Relación con Indicator ENL
    @OneToOne
    @JoinColumn(name = "psnr_indicator_id", nullable = true) // Columna para ENL
    private Indicator PSNR;

    // Relación con Indicator ENL
    @OneToOne
    @JoinColumn(name = "enl_indicator_id", nullable = true) // Columna para ENL
    private Indicator ENL;

    // Relación con Indicator SSI
    @OneToOne
    @JoinColumn(name = "ssi_indicator_id", nullable = true) // Columna para SSI
    private Indicator SSI;

    @OneToOne
    @JoinColumn(name = "smpi_indicator_id", nullable = true) // Columna para SMPI
    private Indicator SMPI;

    // Relación con Indicator SSIM
    @OneToOne
    @JoinColumn(name = "ssim_indicator_id", nullable = true) // Columna para SSIM
    private Indicator SSIM;

    // Relación con Indicator SRS
    @OneToOne
    @JoinColumn(name = "srs_indicator_id", nullable = true) // Columna para SRS
    private Indicator SRS;

    // Relación con Indicator MACANA
    @OneToOne
    @JoinColumn(name = "macana_indicator_id", nullable = true) // Columna para MACANA
    private Indicator MACANA;

    // Relación con Indicator ENTROPY
    @OneToOne
    @JoinColumn(name = "entropy_indicator_id", nullable = true) // Columna para ENTROPY
    private Indicator ENTROPY;

    // Relación con Indicator LUMINANCE
    @OneToOne
    @JoinColumn(name = "luminance_indicator_id", nullable = true) // Columna para LUMINANCE
    private Indicator LUMINANCE;

    // Relación con Indicator CONTRAST
    @OneToOne
    @JoinColumn(name = "contrast_indicator_id", nullable = true) // Columna para CONTRAST
    private Indicator CONTRAST;

    // Relación con Indicator STRUCTURE
    @OneToOne
    @JoinColumn(name = "structure_indicator_id", nullable = true) // Columna para STRUCTURE
    private Indicator STRUCTURE;

}

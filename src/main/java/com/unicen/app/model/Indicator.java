package com.unicen.app.model;


import com.unicen.core.model.AuditableModel;
import com.unicen.core.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "indicator")
@AllArgsConstructor
@NoArgsConstructor
public class Indicator extends AuditableModel<Indicator> {

    @Column(name = "mf")
    private Float mf;

    @Column(name = "lee")
    private Float lee;

    @Column(name = "leeR")
    private Float leeR;

    @Column(name = "newLeeR")
    private Float newLeeR;

    @OneToOne(mappedBy = "ENL", fetch = FetchType.LAZY)
    private Metric metricENL;

    @OneToOne(mappedBy = "SSI", fetch = FetchType.LAZY)
    private Metric metricSSI;
}



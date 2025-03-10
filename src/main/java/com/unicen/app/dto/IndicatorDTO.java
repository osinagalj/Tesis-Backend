package com.unicen.app.dto;

import com.unicen.app.model.Indicator;
import com.unicen.core.model.DtoAble;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndicatorDTO implements DtoAble<IndicatorDTO> {
    private Float mf;
    private Float lee;
    private Float leeR;
    private Float newLeeR;
}
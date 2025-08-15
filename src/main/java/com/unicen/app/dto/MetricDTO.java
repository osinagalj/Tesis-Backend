package com.unicen.app.dto;

import com.unicen.app.model.Indicator;
import com.unicen.core.model.DtoAble;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MetricDTO implements DtoAble<MetricDTO> {
    Long OriginalId;
    int ratio;
    IndicatorDTO PSNR;
    IndicatorDTO SSI;
    IndicatorDTO ENL;
    IndicatorDTO SMPI;
    // Nuevos indicadores
    IndicatorDTO SSIM;
    IndicatorDTO SRS;
    IndicatorDTO MACANA;
    IndicatorDTO ENTROPY;
    IndicatorDTO LUMINANCE;
    IndicatorDTO CONTRAST;
    IndicatorDTO STRUCTURE;
}
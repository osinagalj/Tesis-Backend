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
    int radius;
    Indicator PSNR;
    Indicator ENL;
}
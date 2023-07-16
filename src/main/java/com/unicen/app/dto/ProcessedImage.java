package com.unicen.app.dto;

import com.unicen.core.model.DtoAble;
import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedImage {
    private String originalExternalId;
    private byte[] image;
    private int ratio;
    private String algorithm;
}

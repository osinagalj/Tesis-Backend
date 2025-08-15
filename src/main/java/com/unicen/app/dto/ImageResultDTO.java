package com.unicen.app.dto;

import com.unicen.core.model.DtoAble;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageResultDTO implements DtoAble<ImageResultDTO> {
    private String externalId;
    private String type;
    private String algorithm = "default alg";
    private Integer ratio = 10;
    private String name;
}

package com.unicen.app.dto;

import com.unicen.core.model.DtoAble;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO implements DtoAble<ImageDTO> {
    private String externalId;
    private int width;
    private int height;
    @NonNull
    private String url;
    private String type;
    private String description;
}

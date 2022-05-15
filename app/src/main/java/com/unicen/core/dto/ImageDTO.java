package com.unicen.core.dto;

import com.unicen.core.model.ExternalDtoAble;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class ImageDTO implements ExternalDtoAble<ImageDTO> {
    private String type;
    @NonNull private String url;
    private String externalId;
    private String description;
    private int width;
    private int height;

    public ImageDTO() {
    }
}
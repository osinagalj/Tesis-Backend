package com.ecofy.app.dto;

import com.ecofy.core.model.DtoAble;
import lombok.*;

import java.sql.Timestamp;

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
    private String name;
    private Timestamp createdAt;
}

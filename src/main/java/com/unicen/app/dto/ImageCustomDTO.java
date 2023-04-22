package com.unicen.app.dto;

import com.unicen.core.model.DtoAble;
import lombok.*;
import org.springframework.core.io.ByteArrayResource;

import java.io.InputStream;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ImageCustomDTO implements DtoAble<ImageCustomDTO> {
    private String externalId;
    private int width;
    private int height;
    @NonNull
    private String url;
    private String type;
    private String description;
    private ByteArrayResource inputStream;


}

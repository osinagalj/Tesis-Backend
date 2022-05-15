package com.unicen.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamicPropertyCreationDTO {
    private String key;
    private String content;
    private long sinceVersion;

    public DynamicPropertyCreationDTO(String key, String content, long sinceVersion) {
        this.key = key;
        this.content = content;
        this.sinceVersion = sinceVersion;
    }
}
package com.unicen.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DynamicPropertyDTO {
    private String key;
    private String content;
    private long sinceVersion;

}
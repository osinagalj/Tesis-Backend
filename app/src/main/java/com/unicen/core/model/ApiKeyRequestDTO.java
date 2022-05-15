package com.unicen.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiKeyRequestDTO {
    private String keyValue;
    private List<String> scopes;
}

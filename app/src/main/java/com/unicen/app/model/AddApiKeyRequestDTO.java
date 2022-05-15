package com.unicen.app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddApiKeyRequestDTO {
    private String email;
    private List<String> scopes;
}

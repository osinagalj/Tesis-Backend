package com.unicen.core.controller;

import com.unicen.core.dto.ApiResultDTO;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Api(tags = "1. Status")
@RequestMapping("/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<ApiResultDTO<String>> status() {
        return ResponseEntity.ok(ApiResultDTO.ofSuccess("OK"));
    }

    @GetMapping("/with-auth")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<ApiResultDTO<String>> statusWIthToken() {
        return ResponseEntity.ok(ApiResultDTO.ofSuccess("OK"));
    }


}

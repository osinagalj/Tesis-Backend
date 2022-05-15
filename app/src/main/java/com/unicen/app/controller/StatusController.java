package com.unicen.app.controller;


import com.unicen.app.dto.ApiResultDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//MOVE to ops
@Controller
@RequestMapping("/status")
public class StatusController {

    @GetMapping
    public ResponseEntity<ApiResultDTO<String>> status() {
        return ResponseEntity.ok(ApiResultDTO.ofSuccess("OK"));
    }

}

package com.ecofy.app.controller;

import com.ecofy.core.services.EmailService;
import com.ecofy.core.dto.ApiResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PreAuthorize("permitAll()")
@RequestMapping("/send-email")
public class SendEmail {

    @Autowired
    EmailService sendEmailService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<ApiResultDTO<String>> status() {
        //sendEmailService.sendEmail("", "osinagalj@gmail.com", "Test");
        return ResponseEntity.ok(ApiResultDTO.ofSuccess("Email has been sent!"));
    }

}

package com.unicen.app.controller;

import com.unicen.app.service.SendEmailService;
import com.unicen.core.dto.ApiResultDTO;
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
    SendEmailService sendEmailService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<ApiResultDTO<String>> status() {
        sendEmailService.sendEmail();
        return ResponseEntity.ok(ApiResultDTO.ofSuccess("Email has been sent!"));
    }

}

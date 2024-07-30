package com.unicen.app;

import com.unicen.core.services.ApplicationPropertiesService;
import com.unicen.core.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class b {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ApplicationPropertiesService properties;


    @Test
    public void test() throws URISyntaxException, IOException {

        String template = properties.getValidationEmailTemplate();
        template = template.replace("${link}", properties.getValidationBaseUrl() + "?code=1234");
        System.out.println("testing..");
        Map<String, String> images = new HashMap<>();
        emailService.sendEmail(properties.getFromEmail(), "osinagalj@gmail.com", "Login", images, template);
    }

}
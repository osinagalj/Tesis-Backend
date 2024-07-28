package com.ecofy;

import com.ecofy.core.spring.lightweightcontainer.GlobalApplicationContext;
import com.ecofy.core.model.User;
import com.ecofy.core.services.AuthenticationService;
import com.ecofy.core.services.EntitiesDrawer;
import com.ecofy.core.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile({"dev", "prod"})
public class AppBackendDevInitializer {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private GlobalApplicationContext globalApplicationContext; // to force Spring instantiation

    @PostConstruct
    private void initialize() {

        User admin = EntitiesDrawer.adminUser();
        String token = getAuthenticationService().loginUsingPassword(admin.getEmail(), "password").getToken();
        logger.info("session token: " + token);

        User userUser = EntitiesDrawer.userUser();
        String tokenUser = getAuthenticationService().loginUsingPassword(userUser.getEmail(), "password").getToken();
        logger.info("session token: " + tokenUser);


    }

    public static AuthenticationService getAuthenticationService() {
        return (AuthenticationService) GlobalApplicationContext.getBean("authenticationService");
    }
}

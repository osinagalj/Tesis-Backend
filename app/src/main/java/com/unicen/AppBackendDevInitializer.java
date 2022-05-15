package com.unicen;

import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import com.unicen.core.model.User;
import com.unicen.core.services.AuthenticationService;
import com.unicen.core.services.EntitiesDrawer;
import com.unicen.core.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("dev")
public class AppBackendDevInitializer {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private GlobalApplicationContext globalApplicationContext; // to force Spring instantiation

    @PostConstruct
    private void initialize() {

        User admin = EntitiesDrawer.adminUser();
        String token = getAuthenticationService().loginUsingPassword(admin.getEmail(), "password").getToken();
        logger.info("session token: " + token);

    }

    public static AuthenticationService getAuthenticationService() {
        return (AuthenticationService) GlobalApplicationContext.getBean("authenticationService");
    }
}

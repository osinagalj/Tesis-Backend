package com.unicen.app;

import javax.annotation.PostConstruct;

public class AppBackendDevInitializer {

    @PostConstruct
    private void initialize() {
        System.out.println("init data..");
    }
}

package com.unicen;

import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import nu.pattern.OpenCV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

    @Autowired
    private GlobalApplicationContext globalApplicationContext; // to force Spring instantiation

//    static {
//        OpenCV.loadLocally();
//        }

//    TODO this needs to be to run the opencv library
    public static void main(String[] args) {
        System.out.println("OPENCV" + org.opencv.core.Core.VERSION);
        SpringApplication.run(AppApplication.class, args);
    }

}

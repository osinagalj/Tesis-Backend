package com.unicen;

import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import org.opencv.core.Core;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {

    @Autowired
    private GlobalApplicationContext globalApplicationContext; // to force Spring instantiation

    static {
        String path = System.getProperty("user.dir") + "/opencv/libopencv_java481.dylib";
        System.load(path);
    }
    public static void main(String[] args) {
        System.out.println("OPENCV" + org.opencv.core.Core.VERSION);
        SpringApplication.run(AppApplication.class, args);
    }

}

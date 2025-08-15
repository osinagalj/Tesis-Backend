package com.unicen;

import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class AppApplication {

	@Autowired private GlobalApplicationContext globalApplicationContext; // to force Spring instantiation



	public static void main(String[] args) {
		System.out.println("OPENCV" + org.opencv.core.Core.VERSION);
		SpringApplication.run(AppApplication.class, args);
	}

}

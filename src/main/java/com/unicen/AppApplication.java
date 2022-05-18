package com.unicen;

import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class AppApplication {

	@Autowired private GlobalApplicationContext globalApplicationContext; // to force Spring instantiation

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

}

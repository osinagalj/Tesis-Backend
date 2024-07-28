package com.ecofy.app;

import com.ecofy.app.model.ImageType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AppApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void test(){
		System.out.println("testing..");
		assertEquals(ImageType.PNG.getString(), ImageType.getType("png"));
	}

}

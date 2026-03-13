package com.unicen.app.bmp;

import org.junit.jupiter.api.Test;

import java.io.InputStream;


public class LeeRobustTest {



    public static String ORIGINAL_BMP_IMAGE = "/static/bmp/planeBMP.bmp";

    public static String DESTINATION_BMP_PATH = "src/test/resources/static/generated/01_bmp";


    @Test
    public void LeeRobust() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_BMP_IMAGE);

    }



}

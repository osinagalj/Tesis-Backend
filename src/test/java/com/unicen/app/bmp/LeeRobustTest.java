package com.unicen.app.bmp;

import com.unicen.CapturedImage;
import com.unicen.app.algorithms.LeeFilter;
import com.unicen.app.algorithms.LeeRobustFilter;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class LeeRobustTest {



    public static String ORIGINAL_BMP_IMAGE = "/static/bmp/planeBMP.bmp";

    public static String DESTINATION_BMP_PATH = "src/test/resources/static/generated/01_bmp";


    @Test
    public void LeeRobust() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_BMP_IMAGE);

    }



}

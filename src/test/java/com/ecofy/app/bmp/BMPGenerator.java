package com.ecofy.app.bmp;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class BMPGenerator {
    public static long random = new Date().getTime();
    public static String DESTINATION_JPG_TO_BMP_PATH = "src/test/resources/static/generated/generatedBMP" + random + ".bmp";

    // create the binary mapping
    byte BLACK = (byte)0, WHITE = (byte)255;
    byte[] map = {BLACK, WHITE};

    //This create the image using grey scale, as the images provided
    @Test
    public void createBMPGreyScale_1() throws IOException {
        // Crea un BufferedImage en escala de grises
        int w = 5, h = 5;
        int[] data = {
                0, 0, 0, 0, 0,
                0, WHITE, WHITE, WHITE, 0,
                0, 0, WHITE, 0, 0,
                0, WHITE, WHITE, WHITE, 0,
                0, 0, 0, 0, 0
        };
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        // Obtiene el raster para configurar los píxeles
        WritableRaster raster = bi.getRaster();

        // Establece los píxeles en el raster
        raster.setPixels(0, 0, w, h, data);

        // Guarda la imagen en un archivo BMP
        ImageIO.write(bi, "bmp", new File(DESTINATION_JPG_TO_BMP_PATH));

        System.out.println("Imagen BMP en escala de grises creada con éxito en: " + DESTINATION_JPG_TO_BMP_PATH);
    }

    //This create the image using grey scale, as the images provided
    @Test
    public void createBMPGreyScale_2() throws IOException {
        // Crea un BufferedImage en escala de grises
        int w = 6, h = 5;
        int[] data = {
                WHITE, 0, 0, 0, 0, 0,
                0, WHITE, 0, 0, 0, 0,
                0, 0, WHITE, 0, 0, 0,
                0, 0, 0, WHITE, 0, 0,
                0, 0, 0, 0, WHITE, 0
        };

        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        // Obtiene el raster para configurar los píxeles
        WritableRaster raster = bi.getRaster();


        // Establece los píxeles en el raster
        raster.setPixels(0, 0, w, h, data);

        // Guarda la imagen en un archivo BMP
        ImageIO.write(bi, "bmp", new File(DESTINATION_JPG_TO_BMP_PATH));

        System.out.println("Imagen BMP en escala de grises creada con éxito en: " + DESTINATION_JPG_TO_BMP_PATH);
    }

    //This create the image using grey scale, as the images provided
    @Test
    public void createBMPGreyScale_3() throws IOException {
        // Crea un BufferedImage en escala de grises
        int w = 5, h = 5;

        int[] data = {
                WHITE, 0, 0, 0, WHITE,
                0, WHITE, 0, WHITE, 0,
                0, 0, WHITE, 0, 0,
                0, WHITE, 0, WHITE, 0,
                WHITE, 0, 0, 0, WHITE
        };
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        // Obtiene el raster para configurar los píxeles
        WritableRaster raster = bi.getRaster();

        // Establece los píxeles en el raster
        raster.setPixels(0, 0, w, h, data);

        // Guarda la imagen en un archivo BMP
        ImageIO.write(bi, "bmp", new File(DESTINATION_JPG_TO_BMP_PATH));

        System.out.println("Imagen BMP en escala de grises creada con éxito en: " + DESTINATION_JPG_TO_BMP_PATH);
    }

    /*
    //this create a bmp image from bytes, but it's no in grey scale
    @Test
    public void createImageWhiteAndBlack() throws IOException {
        // create the binary mapping
        IndexColorModel icm = new IndexColorModel(1, map.length, map, map, map);

        // create checkered data
        int[] data = new int[w*h];

        // create image from color model and data
        WritableRaster raster = icm.createCompatibleWritableRaster(w, h);
        raster.setPixels(0, 0, w, h, data);
        BufferedImage bi = new BufferedImage(icm, raster, false, null);

        // output to a file
        ImageIO.write(bi, "bmp", new File(DESTINATION_JPG_TO_BMP_PATH));
    }


    public int[] fillAsChessBoard(int[] data){

        for(int i=0; i<w; i++)
            for(int j=0; j<h; j++)
                data[i*h + j] = i%4<2 && j%4<2 || i%4>=2 && j%4>=2 ? BLACK:WHITE;
        return data;
    }

*/

}
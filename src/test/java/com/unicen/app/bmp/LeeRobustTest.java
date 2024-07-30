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

    LeeFilter leeFilter = new LeeFilter();

    LeeRobustFilter leeRobustFilter = new LeeRobustFilter();

    public static String ORIGINAL_BMP_IMAGE = "/static/bmp/3GreyScale.bmp";

    public static String DESTINATION_BMP_PATH = "src/test/resources/static/generated/01_bmp";

    public static int PER_INF = 0;
    public static int PER_SUP = 0;

    @Test
    public void LeeRobust() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_BMP_IMAGE);
        int[][] new2d = convertBMPToArray(inputStream);

        int height = new2d.length;
        int width = new2d[0].length;

        for(int i=1; i<=5; i++){
            long random = new Date().getTime();
            CapturedImage capturedImage = new CapturedImage(width, height, 0, 0);
            capturedImage.setImageValues(new2d);
            CapturedImage newImageAfterFilter = leeRobustFilter.leeRobustPercentileVersion(capturedImage, i, PER_INF, PER_SUP);
            writeArrayToBMP(newImageAfterFilter.getImageValues(), DESTINATION_BMP_PATH + "_robust_radius_" + i + "_" +  random + ".bmp");

        }
    }


    public static void writeArrayToBMP(int[][] pixelArray, String filePath) throws IOException {
        int height = pixelArray.length;
        int width = pixelArray[0].length;

        // Crear un BufferedImage con las dimensiones correspondientes
        BufferedImage bmpImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Asignar los valores de píxeles del array 2D al BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = pixelArray[x][y];
                // Convertir el valor de píxel de escala de grises a RGB (en este caso, asignamos el mismo valor a los tres canales)
                int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
                bmpImage.setRGB(x, y, rgb);
            }
        }

        // Escribir el BufferedImage en un archivo de imagen BMP
        File bmpFile = new File(filePath);
        ImageIO.write(bmpImage, "bmp", bmpFile);

        System.out.println("Imagen BMP escrita correctamente en: " + filePath);
    }

    public static int[][] convertBMPToArray(InputStream file) throws IOException {
        BufferedImage bmpImage = ImageIO.read(file);

        int width = bmpImage.getWidth();
        int height = bmpImage.getHeight();

        int[][] pixelArray = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Get the RGB value of the pixel at (x, y)
                int rgb = bmpImage.getRGB(x, y);
                // Convert RGB to grayscale by averaging the color channels
                int gray = (int) ((0.3 * ((rgb >> 16) & 0xFF)) + (0.59 * ((rgb >> 8) & 0xFF)) + (0.11 * (rgb & 0xFF)));
                // Store the grayscale value in the pixel array
                pixelArray[x][y] = gray;
            }
        }

        return pixelArray;
    }
}

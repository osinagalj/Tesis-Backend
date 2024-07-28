package com.ecofy.app.bmp;


import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class ReadBMPImageTest {

    public static long random = new Date().getTime();

    public static String ORIGINAL_BMP_IMAGE = "/static/bmp/1GreyScale.bmp";

    public static String DESTINATION_BMP_PATH = "src/test/resources/static/generated/01_bmp" + random + ".bmp";


    //using array
    @Test
    public void readImageToInputSteamAndSaveItWithSameSizeAndArray() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_BMP_IMAGE);
        int[][] new2d = convertBMPToArray(inputStream);
        writeArrayToBMP(new2d, DESTINATION_BMP_PATH);
    }


    public static void writeArrayToBMP(int[][] pixelArray, String filePath) throws IOException {
        int height = pixelArray.length;
        int width = pixelArray[0].length;

        // Crear un BufferedImage con las dimensiones correspondientes
        BufferedImage bmpImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Asignar los valores de píxeles del array 2D al BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = pixelArray[y][x];
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

package com.unicen.core.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BMPUtils {

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

    public static byte[] getImageBytes(BufferedImage image, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, formatName, baos);
        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();
        return imageBytes;
    }

    public  static byte[] removeHalfImage(InputStream imageStream, Color color) throws IOException {
        // Leer la imagen del InputStream

        System.out.println("Reading image");

        BufferedImage image = ImageIO.read(imageStream);
        var formats= ImageIO.getReaderFormatNames();
        // Get the width and height of the image
        int width = image.getWidth();
        int height = image.getHeight();

        // Iterate through the pixels and set the right half of the image to the given color
        for (int y = 0; y < height; y++) {
            for (int x = width / 2; x < width; x++) {
                image.setRGB(x, y, color.getRGB());
            }
        }

        // Save the modified image
        // You can specify the output path and format here

        String desktopPath = System.getProperty("user.home") + "\\Desktop\\";
        String filePath = desktopPath + "test" + Math.random() + ".png";


        // Create a ByteArrayOutputStream to store the image bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //ImageIO.write(image, "png", new File(filePath));
        ImageIO.write(image, "png", outputStream);


        // Obtener los bytes de la imagen del ByteArrayOutputStream
        byte[] imageBytes = outputStream.toByteArray();

        return imageBytes;

    }

}

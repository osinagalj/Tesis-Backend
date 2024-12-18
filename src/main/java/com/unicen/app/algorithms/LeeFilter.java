package com.unicen.app.algorithms;

import com.unicen.CapturedImage;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;

public class LeeFilter extends AlgorithmFilter{


    public static String ORIGINAL_IMAGE = "/static/generated/01.jpg";
    public static String DESTINATION_PATH = "src/main/resources/static/generated/nueva.png";


    public static CapturedImage execute(CapturedImage item, int radius) {
        // Get the dimensions of the image
        int H = item.getHeight();  // Height of the image
        int W = item.getWidth();   // Width of the image

        // Create a new image to store the result of the filter
        CapturedImage aux = new CapturedImage(H, W, item.getDepth(), item.getChannels());

        int nc;  // Number of cells in the neighborhood of the current pixel
        double v; // Pixel value
        double y; // Filtered pixel value

        // Compute the global mean of the image
        double global_mean = 0;
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = item.getValueImage(row, col); // Get the pixel value at (row, col)
                global_mean += v; // Add the pixel value to the total sum for the mean
            }
        }
        // Divide by the total number of pixels to get the global mean
        global_mean /= (H * W);

        // Compute the global variance of the image
        double global_square_sum = 0;
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = item.getValueImage(row, col); // Get the pixel value
                // Add the squared difference between the pixel value and the global mean
                global_square_sum += (v - global_mean) * (v - global_mean);
            }
        }
        // Divide by the total number of pixels to get the global variance
        double global_variance = global_square_sum / (H * W);

        // Apply the filter to each pixel in the image
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                // Compute the number of cells in the neighborhood of the current pixel
                nc = numberCells(item, row, col, radius);

                // Compute the local mean around the pixel (within the specified radius)
                double local_mean = 0;
                for (int i = -radius; i <= radius; i++) {
                    for (int j = -radius; j <= radius; j++) {
                        // Ensure the index is within the image boundaries
                        if ((row + i >= 0) && (col + j >= 0) && (row + i < H) && (col + j < W)) {
                            v = item.getValueImage(row + i, col + j); // Get the pixel value in the neighborhood
                            local_mean += v; // Add the pixel value to the total sum for the local mean
                        }
                    }
                }
                // Divide by the number of cells in the neighborhood to get the local mean
                local_mean /= nc;

                // Compute the local variance in the neighborhood around the pixel
                double local_square_sum = 0;
                for (int i = -radius; i <= radius; i++) {
                    for (int j = -radius; j <= radius; j++) {
                        // Ensure the index is within the image boundaries
                        if ((row + i >= 0) && (col + j >= 0) && (row + i < H) && (col + j < W)) {
                            v = item.getValueImage(row + i, col + j); // Get the pixel value in the neighborhood
                            // Add the squared difference between the pixel value and the local mean
                            local_square_sum += (v - local_mean) * (v - local_mean);
                        }
                    }
                }
                // Divide by the number of cells in the neighborhood to get the local variance
                double local_variance = local_square_sum / nc;

                // Apply the filter to get the adjusted pixel value
                v = item.getValueImage(row, col); // Get the original pixel value
                // Compute the new pixel value using the local mean and variance
                y = local_mean + (v - local_mean) * (local_variance / (local_variance + global_variance));
                aux.setValueImage(row, col, (int) y); // Store the filtered value in the new image (explicit cast to int)
            }
        }

        // Return the filtered image
        return aux;
    }




    public void execute() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_IMAGE);
        BufferedImage imagen = ImageIO.read(inputStream);
        inputStream.close();

        // Convertir la imagen a escala de grises y obtener los valores de los píxeles
        int[][] array2D = convertirAGrisesYObtenerArray(imagen);

        // Guardar la imagen en escala de grises
        saveImageFromArray(array2D, imagen.getWidth(), imagen.getHeight(), DESTINATION_PATH);
    }

    // Convertir la imagen a escala de grises y obtener los valores de los píxeles
    public int[][] convertirAGrisesYObtenerArray(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        int[][] array2D = new int[alto][ancho];

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int color = imagen.getRGB(x, y);
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = color & 0xFF;
                int gris = (r + g + b) / 3;
                array2D[y][x] = gris;
            }
        }
        return array2D;
    }

    // Guardar la imagen a partir de una matriz de píxeles en escala de grises
    public void saveImageFromArray(int[][] array2D, int ancho, int alto, String nombreArchivo) throws IOException {
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int valor = array2D[y][x];
                int rgb = (valor << 16) | (valor << 8) | valor;
                imagen.setRGB(x, y, rgb);
            }
        }

        // Guardar la imagen en un archivo
        File outputFile = new File(nombreArchivo);
        ImageIO.write(imagen, "jpg", outputFile);
    }

    // Método para convertir una matriz de enteros a un InputStream
    public InputStream convertIntArrayToImage(int[][] pixelArray) throws IOException {
        int ancho = pixelArray[0].length;
        int alto = pixelArray.length;
        byte[] byteArray = serializeIntArray(pixelArray);

        // Paso 2: Crea un InputStream a partir del array de bytes
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        return inputStream;
    }

    // Método para serializar una matriz de enteros en un array de bytes
    private static byte[] serializeIntArray(int[][] intArray) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
        objectOutputStream.writeObject(intArray);
        objectOutputStream.close();
        return byteOutputStream.toByteArray();
    }

}

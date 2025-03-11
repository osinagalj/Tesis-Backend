package com.unicen.app.service;

import com.unicen.CapturedImage;
import com.unicen.app.model.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Metrics {



    public static float PSNR(CapturedImage original, CapturedImage result, int i) {
        Mat I1 = intArrayToMat(original.getImageValues());
        Mat I2 = intArrayToMat(result.getImageValues());

        // Make sure images have the same size and type
        if (I1.size().equals(I2.size()) && I1.type() == I2.type()) {

            // Compute absolute difference between images
            Mat s1 = new Mat();
            Core.absdiff(I1, I2, s1);  // |I1 - I2|

            s1.convertTo(s1, CvType.CV_32F);  // Convert to 32-bit float
            s1 = s1.mul(s1);  // |I1 - I2|^2

            // Sum all channels
            Scalar s = Core.sumElems(s1);  // Sum of elements per channel

            double sse = s.val[0] + s.val[1] + s.val[2];  // Sum of channels

            // If sum of squared error (sse) is close to zero, return 0
            if (sse <= 1e-10) {
                return 0;
            } else {
                // Calculate MSE (Mean Squared Error)
                double mse = sse / (double) (I1.channels() * I1.total());
                // Calculate PSNR
                double psnr = 10.0 * Math.log10((255 * 255) / mse);
                return (float )psnr;
            }
        } else {
            return 0.00001f;
        }


    }
    public static float ENL(CapturedImage originalExternalId, CapturedImage image, int i) {
        // Get image dimensions
        int W = image.getHeight();
        int H = image.getWidth();

        double global_mean = 0;
        int v;

        // Calculate mean
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = image.getImageValues()[row][col];
                global_mean += v;
            }
        }
        global_mean = global_mean / (H * W);

        // Calculate variance
        double global_square_sum = 0;
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = image.getImageValues()[row][col];
                global_square_sum += (v - global_mean) * (v - global_mean);
            }
        }

        double global_variance = global_square_sum / (H * W);

        // Return ENL
        return (float) ((global_mean * global_mean) / global_variance);
    }

    public static float SSI(CapturedImage I_original, CapturedImage I_filtrada, int i) {
        int H = I_original.getHeight();
        int W = I_original.getWidth();


        double global_mean_O = 0, global_mean_F = 0;
        int v;

        // Calcular la media
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = I_original.getImageValues()[row][col];
                global_mean_O += v;

                v = I_filtrada.getValueImage(row, col);
                global_mean_F += v;
            }
        }
        global_mean_O /= (H * W);
        global_mean_F /= (H * W);

        // Calcular la varianza
        double global_square_sum_O = 0, global_square_sum_F = 0;
        for (int row = 0; row < H; row++) {
            for (int col = 0; col < W; col++) {
                v = I_original.getImageValues()[row][col];
                global_square_sum_O += (v - global_mean_O) * (v - global_mean_O);

                v = I_filtrada.getValueImage(row, col);
                global_square_sum_F += (v - global_mean_F) * (v - global_mean_F);
            }
        }

        double global_variance_O = global_square_sum_O / (H * W);
        double global_variance_F = global_square_sum_F / (H * W);

        // Retornar el valor de SSI
        return (float) ((Math.sqrt(global_variance_F) * global_mean_O) / (Math.sqrt(global_variance_O) * global_mean_F));    }


    public static float SMPI(CapturedImage originalExternalId, CapturedImage result, int i) {
        return 0.35f;
    }
    public static float SSIM(CapturedImage originalExternalId, CapturedImage result, int i) {
        return 0.4f;
    }

    public static float SRS(CapturedImage originalExternalId, CapturedImage result, int i) {
        return 0.5f;
    }
    public static float MACANA(CapturedImage originalExternalId, CapturedImage result, int i) {
        return 0.6f;
    }
    public static float ENTROPY(CapturedImage original, CapturedImage result, int i) {
        return 0.7f;
    }
    public static float LUMINANCE(CapturedImage original, CapturedImage result, int i) {
        return 0.8f;
    }
    public static float CONTRAST(CapturedImage original, CapturedImage result, int i) {
        return 0.9f;
    }
    public static float STRUCTURE(CapturedImage original, CapturedImage result, int i) {
        return 0.10f;
    }

    public static BufferedImage convertCapturedImage(CapturedImage capturedImage){

        var values = capturedImage.getImageValues();
        int width   = capturedImage.getImageValues().length;
        int height   = capturedImage.getImageValues()[0].length;

        // Crear un BufferedImage con las dimensiones correspondientes
        BufferedImage bmpImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Asignar los valores de píxeles del array 2D al BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = values[x][y];
                // Convertir el valor de píxel de escala de grises a RGB (en este caso, asignamos el mismo valor a los tres canales)
                int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
                bmpImage.setRGB(x, y, rgb);
            }
        }

        return bmpImage;
    }

    public static Mat intArrayToMat(int[][] intArray) {
        // Get the height and width of the input 2D array
        int rows = intArray.length;
        int cols = intArray[0].length;

        // Create a new Mat object of the same size as the input 2D array
        // CV_32S: 32-bit signed integer (you can change the type if needed)
        Mat mat = new Mat(rows, cols, CvType.CV_32S);

        // Loop over the 2D array and assign values to the Mat object
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mat.put(i, j, intArray[i][j]);
            }
        }

        return mat; // Return the created Mat object
    }

    public static int[][] convertBMPToArray(byte[] byteArray) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);

        // Usar ImageIO para leer el byte[] como BufferedImage
        BufferedImage bmpImage = ImageIO.read(bais);


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

    // Método para convertir BufferedImage a Mat
    public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
        System.out.println("OPENCV" + org.opencv.core.Core.VERSION);

        // Crear un objeto Mat de OpenCV
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Mat mat = new Mat(height, width, CvType.CV_8UC3);

        // Obtener los píxeles del BufferedImage y copiarlos a Mat
        int[] data = new int[width * height];
        bufferedImage.getRGB(0, 0, width, height, data, 0, width);

        // Convertir el arreglo de datos a un arreglo de bytes en formato OpenCV
        byte[] byteArray = new byte[3 * width * height];
        for (int i = 0; i < data.length; i++) {
            int pixel = data[i];
            byteArray[3 * i] = (byte) ((pixel >> 16) & 0xFF);  // R
            byteArray[3 * i + 1] = (byte) ((pixel >> 8) & 0xFF);   // G
            byteArray[3 * i + 2] = (byte) (pixel & 0xFF);   // B
        }

        // Colocar los datos en el objeto Mat
        mat.put(0, 0, byteArray);

        return mat;
    }
}

package com.unicen.app;

import org.junit.jupiter.api.Test;

import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;

public class ReadImageTest {

    public static long random = new Date().getTime();
    public static String ORIGINAL_IMAGE = "/static/generated/01.jpg";

    public static String ORIGINAL_IMAGE_JPG_24_BITS = "/static/originals/09.jpg";

    public static String DESTINATION_PATH = "src/test/resources/static/generated/new" + random + ".jpg";

    public static String ORIGINAL_BMP_IMAGE = "/static/generated/new02.bmp";

    public static String DESTINATION_BMP_PATH = "src/test/resources/static/generated/08_bmp" + random + ".bmp";

    public static String ORIGINAL_PNG_IMAGE = "/static/originals/00lionPNG.png";

    public static String DESTINATION_PNG_PATH = "src/test/resources/static/generated/lionPNG.png";


    public static String DESTINATION_JPG_TO_BMP_PATH = "src/test/resources/static/generated/01_jpg_to_bmp" + random + ".bmp";
    public static String READ_DESTINATION_JPG_TO_BMP_PATH = "/static/generated/01_jpg_to_bmp" + random + ".bmp";


    //This tests reduce the size of the image using ImageIO
    // We need to use this file type bc it doens loss information when we manipulate teh data
    @Test
    public void convertJPGtOBMP() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_IMAGE);
        BufferedImage pngImage = ImageIO.read(inputStream);
       var type =  pngImage.getType();

        // Crear una nueva imagen en formato BMP
        BufferedImage bmpImage = new BufferedImage(
                pngImage.getWidth(),
                pngImage.getHeight(),
                pngImage.getType());//TODO BufferedImage.TYPE_BYTE_GRAY //for 8 bits
                                            // BufferedImage.TYPE_INT_RGB for png


        // Copiar los píxeles de la imagen PNG a la imagen BMP
        bmpImage.createGraphics().drawImage(pngImage, 0, 0, null);

        //writeImage(bmpImage, 1.0f, "bmp"); TODO this write the image but with more size

        // Escribir la imagen BMP en un archivo
        ImageIO.write(bmpImage, "bmp", new File(DESTINATION_JPG_TO_BMP_PATH));
    }

    //This tests read an bmp image, convert it to array2 and then to bmp image without lossing information

    @Test
    public void readImageButWithOtherSizeBMPWith2dprocess() throws Exception {

        try {
            InputStream is = getClass().getResourceAsStream(ORIGINAL_BMP_IMAGE);
            var array2d = convertBMPToArray(is);
            writeArrayToBMP(array2d, DESTINATION_BMP_PATH);
            //ImageIO.write(bf, "bmp", new File(DESTINATION_BMP_PATH));
        } catch (IOException e) {
            System.out.println("Error");
        }
    }

    public static void writeArrayToBMP(int[][] pixelArray, String filePath) throws IOException {
        int width = pixelArray.length;
        int height = pixelArray[0].length;

        // Crear un BufferedImage con las dimensiones correspondientes
        BufferedImage bmpImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Asignar los valores de píxeles del array 2D al BufferedImage
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
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

    //It works fine for the bmp image
    @Test
    public void readImageButWithOtherSizeBMP() throws Exception {
        try {
            InputStream is = getClass().getResourceAsStream(ORIGINAL_BMP_IMAGE);
            BufferedImage bf = ImageIO.read(is);
            ImageIO.write(bf, "bmp", new File(DESTINATION_BMP_PATH));
        } catch (IOException e) {
            System.out.println("Error");
        }
    }








    @Test
    public void execute() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_IMAGE);
        inputStream.close();
        try {

            InputStream is = getClass().getResourceAsStream(ORIGINAL_IMAGE);
            BufferedImage bf = ImageIO.read(is);
            ImageIO.write(bf, "jpg", new File(DESTINATION_PATH));
        } catch (IOException e) {
            System.out.println("Error");
        }

    }


    BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
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


    //This tests reduce the size of the image using ImageIO
    @Test
    public void readImageButWithOtherSizeJPG() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_IMAGE);
        inputStream.close();
        try {
            InputStream is = getClass().getResourceAsStream(ORIGINAL_IMAGE);
            BufferedImage bf = ImageIO.read(is);
            ImageIO.write(bf, "jpg", new File(DESTINATION_PATH));
        } catch (IOException e) {
            System.out.println("Error");
        }
    }



    //It works fine for the bmp image
    @Test
    public void readImageButWithOtherSizePNG() throws Exception {
        try {
            InputStream is = getClass().getResourceAsStream(ORIGINAL_PNG_IMAGE);
            BufferedImage bf = ImageIO.read(is);
            ImageIO.write(bf, "png", new File(DESTINATION_PNG_PATH));
        } catch (IOException e) {
            System.out.println("Error");
        }
    }



    @Test
    public void resizeUsingJavaAlgo() throws IOException {
        InputStream is = getClass().getResourceAsStream(ORIGINAL_IMAGE);

        File dest = new File(DESTINATION_PATH);

        BufferedImage sourceImage = ImageIO.read(is);
        Graphics2D g2d = sourceImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        g2d.drawImage(sourceImage, 0, 0, sourceImage.getWidth(null), sourceImage.getHeight(null), null);
        dest.createNewFile();
        writeImage(sourceImage, 1.0f, "jpg", DESTINATION_PATH);
    }


    /**
     * Write a JPEG file setting the compression quality.
     *
     * @param image a BufferedImage to be saved
     * @param quality a float between 0 and 1, where 1 means uncompressed.
     * @throws IOException in case of problems writing the file
     */
    private static void writeImage(BufferedImage image, float quality, String format, String fileDest)
            throws IOException {
        ImageWriter writer = null;
        FileImageOutputStream output = null;
        try {
            writer = ImageIO.getImageWritersByFormatName(format).next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality); //TODO only for png?

            output = new FileImageOutputStream(new File(fileDest));
            writer.setOutput(output);
            IIOImage iioImage = new IIOImage(image, null, null);
            writer.write(null, iioImage, param);
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (writer != null) {
                writer.dispose();
            }
            if (output != null) {
                output.close();
            }
        }
    }


    //It works fine for the bmp image
    @Test
    public void readImageButWithOtherSizeBMPWith2d() throws Exception {
        try {
            InputStream is = getClass().getResourceAsStream("/static/generated/08_bmpTest.bmp");
            BufferedImage bf = ImageIO.read(is);
            ImageIO.write(bf, "bmp", new File(DESTINATION_BMP_PATH));
        } catch (IOException e) {
            System.out.println("Error");
        }
    }




    //This test read the input correctly and save the image with the same size and quality
    @Test
    public void readImageToInputSteamAndSaveItWithSameSize() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ORIGINAL_IMAGE);
        try (FileOutputStream out = new FileOutputStream(new File(DESTINATION_PATH))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


}

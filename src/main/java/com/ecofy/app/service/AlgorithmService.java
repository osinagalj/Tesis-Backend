package com.ecofy.app.service;

import com.ecofy.BMPUtils;
import com.ecofy.CapturedImage;
import com.ecofy.app.algorithms.AlgorithmFilter;
import com.ecofy.app.algorithms.LeeFilter;
import com.ecofy.app.dto.ProcessedImage;
import com.ecofy.app.model.Algorithm;
import com.ecofy.core.exceptions.CoreApiException;
import com.ecofy.core.model.User;
import com.ecofy.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.List;

import static com.ecofy.BMPUtils.convertBMPToArray;


@Service
public class AlgorithmService  {

    @Autowired
    UserService userService;

    @Autowired
    ImageService imageService;

    @Autowired
    ImageResultService imageResultService;

    public void process (Algorithm algorithm,String resourceExternalId, Integer ratioFrom, Integer ratioTo, User user) throws IOException {
        Integer ratio = ratioFrom;
        var image =  imageService.findByExternalIdAndFetchImageEagerly(resourceExternalId);
        if(!image.isPresent()){
            throw CoreApiException.objectNotFound("Resource : " + resourceExternalId + " not exists");
        }
        InputStream is = new ByteArrayInputStream(image.get().getImageData());

        while(ratio < ratioTo + 1){

            List<ProcessedImage> response = algorithm.process(resourceExternalId, is, ratio, image.get().getName());
            is.reset();
            response.forEach(
                    r -> {
                        System.out.println("Result one");
                        System.out.println(r.getAlgorithm());
                        System.out.println(r.getRatio());
                        System.out.println(r.getOriginalExternalId());

                        try {
                            imageResultService.saveImage(r.getName() + "-" + r.getAlgorithm() + " - ratio:" + r.getRatio(), r.getAlgorithm(), r.getRatio(),"png",r.getImage(), image.get());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            ratio++;
        }
    }

    public static ProcessedImage algorithmLee (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
       //Aplica algoritmo de lee
       //Guarda la imagen (asociada al resourceExternalid original)
        int[][] array = convertBMPToArray(image);

        CapturedImage capturedImage = new CapturedImage(0,0,0,0);
        capturedImage.setImageValues(array);
        capturedImage.setHeight(array.length);
        capturedImage.setWidth(array[0].length);
        CapturedImage newImage = LeeFilter.execute(capturedImage, ratio);

        var newArray = newImage.getImageValues();
        int width   = newImage.getImageValues().length;
        int height   = newImage.getImageValues()[0].length;

        // Crear un BufferedImage con las dimensiones correspondientes
        BufferedImage bmpImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        // Asignar los valores de píxeles del array 2D al BufferedImage
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = newArray[x][y];
                // Convertir el valor de píxel de escala de grises a RGB (en este caso, asignamos el mismo valor a los tres canales)
                int rgb = (pixelValue << 16) | (pixelValue << 8) | pixelValue;
                bmpImage.setRGB(x, y, rgb);
            }
        }
        ProcessedImage result = new ProcessedImage();
        result.setRatio(ratio);
        result.setOriginalExternalId(originalExternalId);
        result.setImage(BMPUtils.getImageBytes(bmpImage, "bmp"));
        result.setAlgorithm(Algorithm.LEE.getString());
        result.setName(name);

        long random = new Date().getTime();

        String DESTINATION_BMP_PATH = "src/main/resources/static/generated/01_bmp_" + ratio + "  -  " + random + ".bmp";
        File outputfile = new File(DESTINATION_BMP_PATH);
        ImageIO.write(bmpImage, "bmp", outputfile);

        return result;
    }

    public static ProcessedImage algorithmMedian (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        //Aplica algoritmo de Median
        //Guarda la imagen (asociada al resourceExternalid original)
        Color color = new Color(0, 0, 0); //black

        ProcessedImage result = new ProcessedImage();
        result.setRatio(ratio);
        result.setOriginalExternalId(originalExternalId);
        result.setImage(removeHalfImage(image, color));
        result.setAlgorithm(Algorithm.MEDIAN.getString());
        result.setName(name);
        return result;
    }

    public static ProcessedImage algorithmRobustLee (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        //Aplica algoritmo de lee
        //Guarda la imagen (asociada al resourceExternalid original)
        Color color = new Color(255, 0, 0); //black

        ProcessedImage result = new ProcessedImage();
        result.setRatio(ratio);
        result.setOriginalExternalId(originalExternalId);
        result.setImage(removeHalfImage(image, color));
        result.setAlgorithm(Algorithm.ROBUST_LEE.getString());
        result.setName(name);
        return result;
    }


    public  static byte[] removeHalfImage(InputStream imageStream, Color color) throws IOException {
        // Leer la imagen del InputStream

        System.out.println("Reading image");

        BufferedImage image = ImageIO.read(imageStream);
        var formats= ImageIO.getReaderFormatNames();
        // Obtener la anchura y altura de la imagen
        int width = image.getWidth();
        int height = image.getHeight();

        // Iterar a través de los píxeles y establecer la mitad de la imagen como negra
        for (int y = 0; y < height; y++) {
            for (int x = width / 2; x < width; x++) {
                image.setRGB(x, y, color.getRGB());
            }
        }

        // Guardar la imagen modificada
        // Aquí puedes especificar la ruta y el formato de salida de la imagen modificada

        String desktopPath = System.getProperty("user.home") + "\\Desktop\\";
        String filePath = desktopPath + "test" + Math.random() + ".png";


        // Crear un ByteArrayOutputStream para almacenar los bytes de la imagen
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //ImageIO.write(image, "png", new File(filePath));
        ImageIO.write(image, "png", outputStream);


        // Obtener los bytes de la imagen del ByteArrayOutputStream
        byte[] imageBytes = outputStream.toByteArray();

        return imageBytes;

    }

}

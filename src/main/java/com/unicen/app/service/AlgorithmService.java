package com.unicen.app.service;

import com.unicen.app.dto.ProcessedImage;
import com.unicen.app.model.Algorithm;
import com.unicen.app.model.Image;
import com.unicen.app.repository.ImageRepository;
import com.unicen.core.exceptions.CoreApiException;
import com.unicen.core.model.User;
import com.unicen.core.services.PublicObjectCrudService;
import com.unicen.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.util.SignerOverrideUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
        // var a = service.findAll().stream().findFirst();
        if(!image.isPresent()){
            throw CoreApiException.objectNotFound("Resource : " + resourceExternalId + " not exists");
        }
        InputStream is = new ByteArrayInputStream(image.get().getImageData());


        while(ratio < ratioTo + 1){

            List<ProcessedImage> response = algorithm.process(resourceExternalId, is, ratio);
            is.reset();
            response.forEach(
                    r -> {
                        System.out.println("Result one");
                        System.out.println(r.getAlgorithm());
                        System.out.println(r.getRatio());
                        System.out.println(r.getOriginalExternalId());

                        try {
                            imageResultService.saveImage("Imagen prcesada xd" + Math.random(),r.getAlgorithm(),r.getRatio(),"png",r.getImage(),image.get());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            ratio++;
        }
    }

    public static ProcessedImage algorithmLee (InputStream image, Integer ratio, String originalExternalId) throws IOException {
       //Aplica algoritmo de lee
       //Guarda la imagen (asociada al resourceExternalid original)

        Color color = new Color(255, 255, 255); //white
        ProcessedImage result = new ProcessedImage();
        result.setRatio(ratio);
        result.setOriginalExternalId(originalExternalId);
        result.setImage(removeHalfImage(image, color));
        result.setAlgorithm(Algorithm.LEE.getString());

        return result;


    }

    public static ProcessedImage algorithmMedian (InputStream image, Integer ratio, String originalExternalId) throws IOException {
        //Aplica algoritmo de Median
        //Guarda la imagen (asociada al resourceExternalid original)

        Color color = new Color(0, 0, 0); //black

        ProcessedImage result = new ProcessedImage();
        result.setRatio(ratio);
        result.setOriginalExternalId(originalExternalId);
        result.setImage(removeHalfImage(image, color));
        result.setAlgorithm(Algorithm.MEDIAN.getString());

        return result;


    }

    public static ProcessedImage algorithmRobustLee (InputStream image, Integer ratio, String originalExternalId) throws IOException {
        //Aplica algoritmo de lee
        //Guarda la imagen (asociada al resourceExternalid original)
        Color color = new Color(255, 0, 0); //black

        ProcessedImage result = new ProcessedImage();
        result.setRatio(ratio);
        result.setOriginalExternalId(originalExternalId);
        result.setImage(removeHalfImage(image, color));
        result.setAlgorithm(Algorithm.ROBUST_LEE.getString());

        return result;
    }



    public  static byte[] removeHalfImage(InputStream imageStream, Color color) throws IOException {
        // Leer la imagen del InputStream

        System.out.println("rEADING IMAGE" );

        BufferedImage image = ImageIO.read(imageStream);

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

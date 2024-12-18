package com.unicen.app.service;

import com.unicen.BMPUtils;
import com.unicen.CapturedImage;
import com.unicen.app.algorithms.LeeFilter;
import com.unicen.app.algorithms.LeeRobustFilter;
import com.unicen.app.algorithms.MedianFilterHuang;
import com.unicen.app.dto.ProcessedImage;
import com.unicen.app.model.Algorithm;
import com.unicen.core.exceptions.CoreApiException;
import com.unicen.core.model.User;
import com.unicen.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import static com.unicen.BMPUtils.convertBMPToArray;


@Service
public class AlgorithmService  {

    @Autowired
    ImageService imageService;

    @Autowired
    ResultService imageResultService;

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
        int[][] array = convertBMPToArray(image);
        CapturedImage capturedImage = new CapturedImage(array[0].length, array.length, array);

        //Apply lee FILTER
        CapturedImage newImage = LeeFilter.execute(capturedImage, ratio);

        BufferedImage bmpImage = convertCapturedImage(newImage);
        return saveProcessImage(originalExternalId, ratio, bmpImage, name, Algorithm.LEE.getString());
    }

    public static ProcessedImage algorithmMedian (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        int[][] array = convertBMPToArray(image);
        CapturedImage capturedImage = new CapturedImage(array[0].length, array.length, array);

        //Apply Median filter
        CapturedImage newImage = MedianFilterHuang.execute(capturedImage, 0.3d, ratio);

        BufferedImage bmpImage = convertCapturedImage(newImage);
        return saveProcessImage(originalExternalId, ratio, bmpImage, name, Algorithm.MEDIAN.getString());
    }

    public static ProcessedImage algorithmRobustLee (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        int[][] array = convertBMPToArray(image);
        CapturedImage capturedImage = new CapturedImage(array[0].length, array.length, array);

        //Apply Median filter
        CapturedImage newImage = LeeRobustFilter.execute(capturedImage, ratio);

        BufferedImage bmpImage = convertCapturedImage(newImage);
        return saveProcessImage(originalExternalId, ratio, bmpImage, name, Algorithm.ROBUST_LEE.getString());

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

    public static ProcessedImage saveProcessImage(String originalExternalId, int ratio, BufferedImage bmpImage, String name, String algorithm) throws IOException {

        ProcessedImage result = new ProcessedImage();
        result.setRatio(ratio);
        result.setOriginalExternalId(originalExternalId);
        result.setImage(BMPUtils.getImageBytes(bmpImage, "bmp"));
        result.setAlgorithm(algorithm);
        result.setName(name);

        return result;
    }

}

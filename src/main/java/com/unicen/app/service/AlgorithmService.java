package com.unicen.app.service;

import com.unicen.BMPUtils;
import com.unicen.CapturedImage;
import com.unicen.app.algorithms.LeeFilter;
import com.unicen.app.algorithms.LeeRobustFilter;
import com.unicen.app.algorithms.MedianFilterHuang;
import com.unicen.app.dto.ProcessedImage;
import com.unicen.app.model.Algorithm;
import com.unicen.app.model.Image;
import com.unicen.app.model.Indicator;
import com.unicen.app.model.Metric;
import com.unicen.core.exceptions.CoreApiException;
import com.unicen.core.model.User;
import com.unicen.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    MetricService metricService;

    @Autowired
    IndicatorService indicatorService;


    public void process (Algorithm algorithm,String resourceExternalId, Integer ratioFrom, Integer ratioTo, User user) throws IOException {
        Integer ratio = ratioFrom;
        var image =  imageService.findByExternalIdAndFetchImageEagerly(resourceExternalId);
        if(!image.isPresent()){
            throw CoreApiException.objectNotFound("Resource : " + resourceExternalId + " not exists");
        }
        InputStream is = new ByteArrayInputStream(image.get().getImageData());

        while(ratio < ratioTo + 1){
            List<ProcessedImage> response = algorithm.process(resourceExternalId, is, ratio, image.get().getName(), this);
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

    @Transactional
    public void createMetric(Image original, Image result, int i, boolean median, boolean lee, boolean leeR, boolean newLeeR) {
        // Intentar obtener la métrica existente
        var existingMetric = metricService.findByOriginalImageIdAndRatio(original.getId(), i);

        if (existingMetric.isPresent()) {
            // Si ya existe, obtenemos los indicadores
            Indicator indicatorPSNR = existingMetric.get().getPSNR();

            Indicator indicatorENL = existingMetric.get().getENL();
            Indicator indicatorSSI = existingMetric.get().getSSI();
            Indicator indicatorSMPI = existingMetric.get().getSMPI();
            Indicator indicatorSSIM = existingMetric.get().getSSIM();
            Indicator indicatorSRS = existingMetric.get().getSRS();
            Indicator indicatorMACANA = existingMetric.get().getMACANA();
            Indicator indicatorENTROPY = existingMetric.get().getENTROPY();
            Indicator indicatorLUMINANCE = existingMetric.get().getLUMINANCE();
            Indicator indicatorCONTRAST = existingMetric.get().getCONTRAST();
            Indicator indicatorSTRUCTURE = existingMetric.get().getSTRUCTURE();

            // Actualizar solo los valores correspondientes según los booleanos
            updateIndicatorValues(median, lee, leeR, newLeeR, original, i, indicatorENL, indicatorSSI, indicatorSMPI, indicatorSSIM, indicatorSRS, indicatorMACANA, indicatorENTROPY, indicatorLUMINANCE, indicatorCONTRAST, indicatorSTRUCTURE, indicatorPSNR);

        } else {
            // Si no existe la métrica, creamos una nueva
            Metric metric = createNewMetric(original, i, median, lee, leeR, newLeeR);

            // Guardamos la nueva métrica
            metricService.save(metric);
        }
    }

    private void updateIndicatorValues(boolean median, boolean lee, boolean leeR, boolean newLeeR, Image original, int i,
                                       Indicator indicatorENL, Indicator indicatorSSI, Indicator indicatorSMPI,
                                       Indicator indicatorSSIM, Indicator indicatorSRS, Indicator indicatorMACANA,
                                       Indicator indicatorENTROPY, Indicator indicatorLUMINANCE, Indicator indicatorCONTRAST,
                                       Indicator indicatorSTRUCTURE, Indicator indicatorPSNR) {
        // Solo actualizamos si el booleano correspondiente es true

        if (lee) {
            updateIndicatorField(indicatorPSNR, original, i, "Lee");
            updateIndicatorField(indicatorENL, original, i, "Lee");
            updateIndicatorField(indicatorSSI, original, i, "Lee");
            updateIndicatorField(indicatorSMPI, original, i, "Lee");
            updateIndicatorField(indicatorSSIM, original, i, "Lee");
            updateIndicatorField(indicatorSRS, original, i, "Lee");
            updateIndicatorField(indicatorMACANA, original, i, "Lee");
            updateIndicatorField(indicatorENTROPY, original, i, "Lee");
            updateIndicatorField(indicatorLUMINANCE, original, i, "Lee");
            updateIndicatorField(indicatorCONTRAST, original, i, "Lee");
            updateIndicatorField(indicatorSTRUCTURE, original, i, "Lee");
        }

        if (median) {
            updateIndicatorField(indicatorPSNR, original, i, "Mf");
            updateIndicatorField(indicatorENL, original, i, "Mf");
            updateIndicatorField(indicatorSSI, original, i, "Mf");
            updateIndicatorField(indicatorSMPI, original, i, "Mf");
            updateIndicatorField(indicatorSSIM, original, i, "Mf");
            updateIndicatorField(indicatorSRS, original, i, "Mf");
            updateIndicatorField(indicatorMACANA, original, i, "Mf");
            updateIndicatorField(indicatorENTROPY, original, i, "Mf");
            updateIndicatorField(indicatorLUMINANCE, original, i, "Mf");
            updateIndicatorField(indicatorCONTRAST, original, i, "Mf");
            updateIndicatorField(indicatorSTRUCTURE, original, i, "Mf");
        }

        if (leeR) {
            updateIndicatorField(indicatorPSNR, original, i, "LeeR");

            updateIndicatorField(indicatorENL, original, i, "LeeR");
            updateIndicatorField(indicatorSSI, original, i, "LeeR");
            updateIndicatorField(indicatorSMPI, original, i, "LeeR");
            updateIndicatorField(indicatorSSIM, original, i, "LeeR");
            updateIndicatorField(indicatorSRS, original, i, "LeeR");
            updateIndicatorField(indicatorMACANA, original, i, "LeeR");
            updateIndicatorField(indicatorENTROPY, original, i, "LeeR");
            updateIndicatorField(indicatorLUMINANCE, original, i, "LeeR");
            updateIndicatorField(indicatorCONTRAST, original, i, "LeeR");
            updateIndicatorField(indicatorSTRUCTURE, original, i, "LeeR");
        }

        if (newLeeR) {
            updateIndicatorField(indicatorPSNR, original, i, "NewLeeR");
            updateIndicatorField(indicatorENL, original, i, "NewLeeR");
            updateIndicatorField(indicatorSSI, original, i, "NewLeeR");
            updateIndicatorField(indicatorSMPI, original, i, "NewLeeR");
            updateIndicatorField(indicatorSSIM, original, i, "NewLeeR");
            updateIndicatorField(indicatorSRS, original, i, "NewLeeR");
            updateIndicatorField(indicatorMACANA, original, i, "NewLeeR");
            updateIndicatorField(indicatorENTROPY, original, i, "NewLeeR");
            updateIndicatorField(indicatorLUMINANCE, original, i, "NewLeeR");
            updateIndicatorField(indicatorCONTRAST, original, i, "NewLeeR");
            updateIndicatorField(indicatorSTRUCTURE, original, i, "NewLeeR");
        }

        // Guardamos los cambios de indicadores
        indicatorService.save(indicatorENL);
        indicatorService.save(indicatorSSI);
        indicatorService.save(indicatorSMPI);
        indicatorService.save(indicatorSSIM);
        indicatorService.save(indicatorSRS);
        indicatorService.save(indicatorMACANA);
        indicatorService.save(indicatorENTROPY);
        indicatorService.save(indicatorLUMINANCE);
        indicatorService.save(indicatorCONTRAST);
        indicatorService.save(indicatorSTRUCTURE);
    }

    private void updateIndicatorField(Indicator indicator, Image original, int i, String field) {
        // Este método actualiza los valores de los indicadores de acuerdo al campo solicitado
        float value = 0.0f;

        switch (field) {
            case "Lee":
                value = ENL(original, null, i * 2);
                if (indicator != null) indicator.setLee(value);
                break;
            case "Mf":
                value = ENL(original, null, i * 2);
                if (indicator != null) indicator.setMf(value);
                break;
            case "LeeR":
                value = ENL(original, null, i * 2);
                if (indicator != null) indicator.setLeeR(value);
                break;
            case "NewLeeR":
                value = ENL(original, null, i * 2);
                if (indicator != null) indicator.setNewLeeR(value);
                break;
        }
    }

    private Metric createNewMetric(Image original, int i, boolean median, boolean lee, boolean leeR, boolean newLeeR) {
        // Crear un nuevo objeto Metric
        Metric metric = new Metric();
        metric.setOriginalImageId(original);
        metric.setRatio(i);

        // Asignar valores por defecto a los indicadores
        Indicator indicatorPSNR = new Indicator();
        Indicator indicatorENL = new Indicator();
        Indicator indicatorSSI = new Indicator();
        Indicator indicatorSMPI = new Indicator();
        Indicator indicatorSSIM = new Indicator();
        Indicator indicatorSRS = new Indicator();
        Indicator indicatorMACANA = new Indicator();
        Indicator indicatorENTROPY = new Indicator();
        Indicator indicatorLUMINANCE = new Indicator();
        Indicator indicatorCONTRAST = new Indicator();
        Indicator indicatorSTRUCTURE = new Indicator();

        if (lee) {
            indicatorPSNR.setLee(0.2f + i);
            indicatorENL.setLee(0.2f + i);
            indicatorSSI.setLee(0.3f + i);
            indicatorSMPI.setLee(8.3f + i);
            indicatorSSIM.setLee(0.4f + i);
            indicatorSRS.setLee(0.5f + i);
            indicatorMACANA.setLee(0.6f + i);
            indicatorENTROPY.setLee(0.7f + i);
            indicatorLUMINANCE.setLee(0.8f + i);
            indicatorCONTRAST.setLee(0.9f + i);
            indicatorSTRUCTURE.setLee(1.0f + i);
        }
        if (median) {
            indicatorPSNR.setMf(0.2f + i);
            indicatorENL.setMf(0.2f + i);
            indicatorSSI.setMf(0.3f + i);
            indicatorSMPI.setMf(8.3f + i);
            indicatorSSIM.setMf(0.4f + i);
            indicatorSRS.setMf(0.5f + i);
            indicatorMACANA.setMf(0.6f + i);
            indicatorENTROPY.setMf(0.7f + i);
            indicatorLUMINANCE.setMf(0.8f + i);
            indicatorCONTRAST.setMf(0.9f + i);
            indicatorSTRUCTURE.setMf(1.0f + i);
        }

        if (leeR) {
            indicatorPSNR.setLeeR(0.2f + i);
            indicatorENL.setLeeR(0.2f + i);
            indicatorSSI.setLeeR(0.3f + i);
            indicatorSMPI.setLeeR(8.3f + i);
            indicatorSSIM.setLeeR(0.4f + i);
            indicatorSRS.setLeeR(0.5f + i);
            indicatorMACANA.setLeeR(0.6f + i);
            indicatorENTROPY.setLeeR(0.7f + i);
            indicatorLUMINANCE.setLeeR(0.8f + i);
            indicatorCONTRAST.setLeeR(0.9f + i);
            indicatorSTRUCTURE.setLeeR(1.0f + i);
        }

        if (newLeeR) {
            indicatorPSNR.setNewLeeR(0.3f + i);
            indicatorENL.setNewLeeR(0.1f + i);
            indicatorSSI.setNewLeeR(0.4f + i);
            indicatorSMPI.setNewLeeR(8.3f + i);
            indicatorSSIM.setNewLeeR(0.4f + i);
            indicatorSRS.setNewLeeR(0.5f + i);
            indicatorMACANA.setNewLeeR(0.6f + i);
            indicatorENTROPY.setNewLeeR(0.7f + i);
            indicatorLUMINANCE.setNewLeeR(0.6f + i);
            indicatorCONTRAST.setNewLeeR(0.9f + i);
            indicatorSTRUCTURE.setNewLeeR(1.0f + i);
        }

        indicatorService.save(indicatorPSNR);
        indicatorService.save(indicatorENL);
        indicatorService.save(indicatorSSI);
        indicatorService.save(indicatorSMPI);
        indicatorService.save(indicatorSSIM);
        indicatorService.save(indicatorSRS);
        indicatorService.save(indicatorMACANA);
        indicatorService.save(indicatorENTROPY);
        indicatorService.save(indicatorLUMINANCE);
        indicatorService.save(indicatorCONTRAST);
        indicatorService.save(indicatorSTRUCTURE);

        // Asignar los indicadores a la métrica
        metric.setPSNR(indicatorPSNR);

        metric.setENL(indicatorENL);
        metric.setSSI(indicatorSSI);
        metric.setSMPI(indicatorSMPI);
        metric.setSSIM(indicatorSSIM);
        metric.setSRS(indicatorSRS);
        metric.setMACANA(indicatorMACANA);
        metric.setENTROPY(indicatorENTROPY);
        metric.setLUMINANCE(indicatorLUMINANCE);
        metric.setCONTRAST(indicatorCONTRAST);
        metric.setSTRUCTURE(indicatorSTRUCTURE);

        return metric;
    }










    public static float PSNR(Image originalExternalId, Image result, int i) {
        return 0.1f;
    }
    public static float ENL(Image originalExternalId, Image result, int i) {
        return 0.2f;
    }

    public float SSI(Image originalExternalId, Image result, int i) {
        return 0.3f;
    }

    public float SMPI(Image originalExternalId, Image result, int i) {
        return 0.4f;
    }
    public float SRS(Image originalExternalId, Image result, int i) {
        return 0.5f;
    }
    public float MACANA(Image originalExternalId, Image result, int i) {
        return 0.6f;
    }
    public float ENTROPY(Image original, Image result, int i) {
        return 0.7f;
    }
    public float LUMINANCE(Image original, Image result, int i) {
        return 0.8f;
    }
    public float CONTRAST(Image original, Image result, int i) {
        return 0.9f;
    }
    public float STRUCTURE(Image original, Image result, int i) {
        return 0.10f;
    }



    public ProcessedImage algorithmLee (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        int[][] array = convertBMPToArray(image);
        CapturedImage capturedImage = new CapturedImage(array[0].length, array.length, array);

        //Apply lee FILTER
        CapturedImage newImage = LeeFilter.execute(capturedImage, ratio);

        BufferedImage bmpImage = convertCapturedImage(newImage);


        var imageId =  imageService.findByExternalIdAndFetchImageEagerly(originalExternalId);
        createMetric(imageId.get(), null, ratio, false, true, false, false);

        return saveProcessImage(originalExternalId, ratio, bmpImage, name, Algorithm.LEE.getString());
    }

    public ProcessedImage algorithmMedian (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        int[][] array = convertBMPToArray(image);
        CapturedImage capturedImage = new CapturedImage(array[0].length, array.length, array);

        //Apply Median filter
        CapturedImage newImage = MedianFilterHuang.execute(capturedImage, 0.3d, ratio);

        var imageId =  imageService.findByExternalIdAndFetchImageEagerly(originalExternalId);
        createMetric(imageId.get(), null, ratio, true, false, false, false);

        BufferedImage bmpImage = convertCapturedImage(newImage);
        return saveProcessImage(originalExternalId, ratio, bmpImage, name, Algorithm.MEDIAN.getString());
    }

    public ProcessedImage algorithmRobustLee (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        int[][] array = convertBMPToArray(image);
        CapturedImage capturedImage = new CapturedImage(array[0].length, array.length, array);

        //Apply Median filter
        CapturedImage newImage = LeeRobustFilter.execute(capturedImage, ratio);

        var imageId =  imageService.findByExternalIdAndFetchImageEagerly(originalExternalId);
        createMetric(imageId.get(), null, ratio, false, false, true, false);

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

package com.unicen.app.service;

import com.unicen.BMPUtils;
import com.unicen.CapturedImage;
import com.unicen.app.algorithms.LeeFilter;
import com.unicen.app.algorithms.LeeRobustFilter;
import com.unicen.app.algorithms.MedianFilterHuang;
import com.unicen.app.algorithms.NewLeeRobustFilter;
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

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


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
    public void createMetric(Image original, CapturedImage result, int i, boolean median, boolean lee, boolean leeR, boolean newLeeR) {
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
            updateIndicatorValues(median, lee, leeR, newLeeR, original, result, i, indicatorENL, indicatorSSI, indicatorSMPI, indicatorSSIM, indicatorSRS, indicatorMACANA, indicatorENTROPY, indicatorLUMINANCE, indicatorCONTRAST, indicatorSTRUCTURE, indicatorPSNR);

        } else {
            // Si no existe la métrica, creamos una nueva
            Metric metric = createNewMetric(original, result, i, median, lee, leeR, newLeeR);

            // Guardamos la nueva métrica
            metricService.save(metric);
        }
    }



    private void updateIndicatorValues( boolean median, boolean lee, boolean leeR, boolean newLeeR,
                                       Image original, CapturedImage result, int i, Indicator... indicators) {
        // Actualizamos los indicadores según los flags pasados

        if (lee) {
            updateIndicatorField(indicators, original, result, i, "Lee");
        }
        if (median) {
            updateIndicatorField(indicators, original,result, i, "Mf");
        }
        if (leeR) {
            updateIndicatorField(indicators, original, result,i, "LeeR");
        }
        if (newLeeR) {
            updateIndicatorField(indicators, original,result, i, "NewLeeR");
        }

        // Guardamos los cambios de indicadores solo si no son null
        saveNonNullIndicators(indicators);
    }

    private void updateIndicatorField(Indicator[] indicators, Image original, CapturedImage result, int i, String field) {
        for (Indicator indicator : indicators) {
            if (indicator != null) {
                float value = calculateIndicatorValue(original, result, i, indicator);
                switch (field) {
                    case "Lee":
                        indicator.setLee(value);
                        break;
                    case "Mf":
                        indicator.setMf(value);
                        break;
                    case "LeeR":
                        indicator.setLeeR(value);
                        break;
                    case "NewLeeR":
                        indicator.setNewLeeR(value);
                        break;
                }
            }
        }
    }

    private float calculateIndicatorValue(Image original, CapturedImage result, int i, Indicator indicator) {
        String name = indicator.getName();

        // Aquí definimos los cálculos específicos de cada indicador
        if(name.equals("ENL")){
            return ENL(original, result, i); // Calculamos ENL
        }
        if(name.equals("PSNR")){
            return PSNR(original, result, i); // Calculamos PSNR
        }
        if(name.equals("SSI")){
            return SSI(original, result, i); // Calculamos SSI
        }
        if(name.equals("SMPI")){
            return SMPI(original, result, i); // Calculamos SMPI
        }
        if(name.equals("SSIM")){
            return SSIM(original, result, i); // Calculamos SSIM
        }
        if(name.equals("SRS")){
            return SRS(original, result, i); // Calculamos SRS
        }
        if(name.equals("MACANA")){
            return MACANA(original, result, i); // Calculamos MACANA
        }
        if(name.equals("ENTROPY")){
            return ENTROPY(original, result, i); // Calculamos ENTROPY
        }
        if(name.equals("LUMINANCE")){
            return LUMINANCE(original, result, i); // Calculamos LUMINANCE
        }
        if(name.equals("CONTRAST")){
            return CONTRAST(original, result, i); // Calculamos CONTRAST
        }
        if(name.equals("STRUCTURE")){
            return STRUCTURE(original, result, i); // Calculamos STRUCTURE
        }

        // Si no es ninguno de los anteriores, devolvemos un valor por defecto
        return 0.0f; // Valor por defecto si el indicador no se encuentra
    }


    private void saveNonNullIndicators(Indicator[] indicators) {
        for (Indicator indicator : indicators) {
            if (indicator != null) {
                indicatorService.save(indicator);
            }
        }
    }

    private Metric createNewMetric(Image original, CapturedImage result,  int i, boolean median, boolean lee, boolean leeR, boolean newLeeR) {
        // Crear un nuevo objeto Metric
        Metric metric = new Metric();
        metric.setOriginalImageId(original);
        metric.setRatio(i);

        // Asignar valores por defecto a los indicadores
        Indicator indicatorPSNR = new Indicator("PSNR");
        Indicator indicatorENL = new Indicator("ENL");
        Indicator indicatorSSI = new Indicator("SSI");
        Indicator indicatorSMPI = new Indicator("SMPI");
        Indicator indicatorSSIM = new Indicator("SSIM");
        Indicator indicatorSRS = new Indicator("SRS");
        Indicator indicatorMACANA = new Indicator("MACANA");
        Indicator indicatorENTROPY = new Indicator("ENTROPY");
        Indicator indicatorLUMINANCE = new Indicator("LUMINANCE");
        Indicator indicatorCONTRAST = new Indicator("CONTRAST");
//        Indicator indicatorSTRUCTURE = new Indicator();

        if (lee) {
            indicatorPSNR.setLee(PSNR(original, result, i));
            indicatorENL.setLee(ENL(original, result, i));
            indicatorSSI.setLee(SSI(original, result, i));
            indicatorSMPI.setLee(SMPI(original, result, i)); // Usando el método adecuado
            indicatorSSIM.setLee(SSIM(original, result, i)); // Usando el método adecuado
            indicatorSRS.setLee(SRS(original, result, i));   // Usando el método adecuado
            indicatorMACANA.setLee(MACANA(original, result, i)); // Usando el método adecuado
            indicatorENTROPY.setLee(ENTROPY(original, result, i)); // Usando el método adecuado
            indicatorLUMINANCE.setLee(LUMINANCE(original, result, i)); // Usando el método adecuado
            indicatorCONTRAST.setLee(CONTRAST(original, result, i)); // Usando el método adecuado
        }

        if (median) {
            indicatorPSNR.setMf(PSNR(original, result, i));
            indicatorENL.setMf(ENL(original, result, i));
            indicatorSSI.setMf(SSI(original, result, i));
            indicatorSMPI.setMf(SMPI(original, result, i)); // Usando el método adecuado
            indicatorSSIM.setMf(SSIM(original, result, i)); // Usando el método adecuado
            indicatorSRS.setMf(SRS(original, result, i));   // Usando el método adecuado
            indicatorMACANA.setMf(MACANA(original, result, i)); // Usando el método adecuado
            indicatorENTROPY.setMf(ENTROPY(original, result, i)); // Usando el método adecuado
            indicatorLUMINANCE.setMf(LUMINANCE(original, result, i)); // Usando el método adecuado
            indicatorCONTRAST.setMf(CONTRAST(original, result, i)); // Usando el método adecuado
        }

        if (leeR) {
            indicatorPSNR.setLeeR(PSNR(original, result, i));
            indicatorENL.setLeeR(ENL(original, result, i));
            indicatorSSI.setLeeR(SSI(original, result, i));
            indicatorSMPI.setLeeR(SMPI(original, result, i)); // Usando el método adecuado
            indicatorSSIM.setLeeR(SSIM(original, result, i)); // Usando el método adecuado
            indicatorSRS.setLeeR(SRS(original, result, i));   // Usando el método adecuado
            indicatorMACANA.setLeeR(MACANA(original, result, i)); // Usando el método adecuado
            indicatorENTROPY.setLeeR(ENTROPY(original, result, i)); // Usando el método adecuado
            indicatorLUMINANCE.setLeeR(LUMINANCE(original, result, i)); // Usando el método adecuado
            indicatorCONTRAST.setLeeR(CONTRAST(original, result, i)); // Usando el método adecuado
        }

        if (newLeeR) {
            indicatorPSNR.setNewLeeR(PSNR(original, result, i));
            indicatorENL.setNewLeeR(ENL(original, result, i));
            indicatorSSI.setNewLeeR(SSI(original, result, i));
            indicatorSMPI.setNewLeeR(SMPI(original, result, i)); // Usando el método adecuado
            indicatorSSIM.setNewLeeR(SSIM(original, result, i)); // Usando el método adecuado
            indicatorSRS.setNewLeeR(SRS(original, result, i));   // Usando el método adecuado
            indicatorMACANA.setNewLeeR(MACANA(original, result, i)); // Usando el método adecuado
            indicatorENTROPY.setNewLeeR(ENTROPY(original, result, i)); // Usando el método adecuado
            indicatorLUMINANCE.setNewLeeR(LUMINANCE(original, result, i)); // Usando el método adecuado
            indicatorCONTRAST.setNewLeeR(CONTRAST(original, result, i)); // Usando el método adecuado
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
//        indicatorService.save(indicatorSTRUCTURE);

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
//        metric.setSTRUCTURE(indicatorSTRUCTURE);

        return metric;
    }


    public static float PSNR(Image original, CapturedImage result, int i) {
        //        IplImage *im2 = image2->getImage();
//
//        Mat I1 = cvarrToMat(im1);
//        Mat I2 = cvarrToMat(im2);
//
//        Mat s1;
//        absdiff(I1, I2, s1);       // |I1 - I2|
//        s1.convertTo(s1, CV_32F);  // cannot make a square on 8 bits
//        s1 = s1.mul(s1);           // |I1 - I2|^2
//
//        Scalar s = cv::sum( s1 );         // sum elements per channel
//
//        double sse = s.val[0] + s.val[1] + s.val[2]; // sum channels
//
//        if( sse <= 1e-10) // for small values return zero
//            return 0;
//        else{
//            double  mse = sse / (double(I1.channels()) * double(I1.total()));
//            double psnr = 10.0*log10((255*255)/mse);
//            return psnr;
//        }

        return 0.1f;
    }
    public static float ENL(Image originalExternalId, CapturedImage result, int i) {
        return 0.2f;
    }

    public float SSI(Image originalExternalId, CapturedImage result, int i) {
        return 0.3f;
    }


    public float SMPI(Image originalExternalId, CapturedImage result, int i) {
        return 0.35f;
    }
    public float SSIM(Image originalExternalId, CapturedImage result, int i) {
        return 0.4f;
    }

    public float SRS(Image originalExternalId, CapturedImage result, int i) {
        return 0.5f;
    }
    public float MACANA(Image originalExternalId, CapturedImage result, int i) {
        return 0.6f;
    }
    public float ENTROPY(Image original, CapturedImage result, int i) {
        return 0.7f;
    }
    public float LUMINANCE(Image original, CapturedImage result, int i) {
        return 0.8f;
    }
    public float CONTRAST(Image original, CapturedImage result, int i) {
        return 0.9f;
    }
    public float STRUCTURE(Image original, CapturedImage result, int i) {
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

    public ProcessedImage algorithmNewRobustLee (InputStream image, Integer ratio, String originalExternalId, String name) throws IOException {
        int[][] array = convertBMPToArray(image);
        CapturedImage capturedImage = new CapturedImage(array[0].length, array.length, array);

        //Apply Median filter
        CapturedImage newImage = NewLeeRobustFilter.execute(capturedImage, ratio);

        var imageId =  imageService.findByExternalIdAndFetchImageEagerly(originalExternalId);
        createMetric(imageId.get(), null, ratio, false, false, false, true);

        BufferedImage bmpImage = convertCapturedImage(newImage);
        return saveProcessImage(originalExternalId, ratio, bmpImage, name, Algorithm.NEW_LEE_ROBUST.getString());

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

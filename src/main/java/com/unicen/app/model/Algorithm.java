package com.unicen.app.model;

import com.unicen.app.dto.ProcessedImage;
import com.unicen.app.service.AlgorithmService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Algorithm {
    LEE {
        @Override
        public String getString(){
            return "LEE";
        }
        @Override
        public List<ProcessedImage> process(String resourceExternalId, InputStream image, Integer ratio, String name) throws IOException {
            return Arrays.asList(AlgorithmService.algorithmLee(image, ratio, resourceExternalId, name));
        }
    },
    ROBUST_LEE {
        @Override
        public String getString(){
            return "ROBUST_LEE";
        }
        @Override
        public List<ProcessedImage> process(String resourceExternalId, InputStream image, Integer ratio, String name) throws IOException {
            return Arrays.asList(AlgorithmService.algorithmRobustLee(image, ratio, resourceExternalId, name));
        }
    },
    MEDIAN {
        @Override
        public String getString(){
            return "MEDIAN";
        }
        @Override
        public List<ProcessedImage> process(String resourceExternalId, InputStream image, Integer ratio, String name) throws IOException {
            return Arrays.asList(AlgorithmService.algorithmMedian(image, ratio, resourceExternalId, name));
        }
    },
    ALL {
        @Override
        public String getString(){
            return "ALL";
        }
        @Override
        public List<ProcessedImage> process(String resourceExternalId, InputStream image, Integer ratio, String name) throws IOException {
            List<ProcessedImage> result = new ArrayList<>();
            //Use functional programming
            for (Algorithm algorithm : Algorithm.values()) {
                if (algorithm != Algorithm.ALL) {
                    result.addAll(algorithm.process(resourceExternalId, image, ratio, name));
                    image.reset();
                }
            }
            return result;
        }
    };

    public abstract String getString();
    public abstract List<ProcessedImage> process(String resourceExternalId, InputStream image, Integer ratio, String name) throws IOException;

}

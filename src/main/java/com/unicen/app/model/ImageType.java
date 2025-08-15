package com.unicen.app.model;

import java.util.Arrays;

public enum ImageType {
    JPEG {
        @Override
        public String getString(){
            return "JPEG";
        }
    },
    BMP {
        @Override
        public String getString(){
            return "BMP";
        }
    },
    PNG {
        @Override
        public String getString(){
            return "PNG";
        }
    };

    public abstract String getString();

    public static String getType(String type){
        return  Arrays.stream(values())
                .filter(t -> t.getString().equals(type.toUpperCase()))
                .findFirst()
                .orElseThrow()
                .getString();
    }
}

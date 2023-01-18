package com.unicen.app.model;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum ImageType {
    JPEG {
        @Override
        public String getString(){
            return "JPEG";
        }
    },
    PNG{
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

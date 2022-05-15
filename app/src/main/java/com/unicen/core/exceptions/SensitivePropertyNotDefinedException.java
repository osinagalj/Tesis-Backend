package com.unicen.core.exceptions;

public class SensitivePropertyNotDefinedException extends ConfigurationException {

    public SensitivePropertyNotDefinedException(String propertyName) {
        super("Property " + propertyName + " not defined in Secrets Manager");
    }
}

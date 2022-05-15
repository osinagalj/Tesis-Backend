package com.unicen.app.exceptions;

public class SensitivePropertyNotDefinedException extends ConfigurationException {

    public SensitivePropertyNotDefinedException(String propertyName) {
        super("Property " + propertyName + " not defined in Secrets Manager");
    }
}

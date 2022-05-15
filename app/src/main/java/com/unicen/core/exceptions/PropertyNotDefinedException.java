package com.unicen.core.exceptions;

public class PropertyNotDefinedException extends ConfigurationException {

    public PropertyNotDefinedException(String propertyName) {
        super("Property " + propertyName + " not defined");
    }
}

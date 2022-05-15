package com.unicen.app.exceptions;

public class PropertyNotDefinedException extends ConfigurationException {

    public PropertyNotDefinedException(String propertyName) {
        super("Property " + propertyName + " not defined");
    }
}

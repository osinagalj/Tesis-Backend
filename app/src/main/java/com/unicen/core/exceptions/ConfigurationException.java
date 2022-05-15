package com.unicen.core.exceptions;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException() {
    }

    protected ConfigurationException(String message) {
        super(message);
    }


    public static ConfigurationException resourceCannotBeLoaded(String path) {
        return new ConfigurationException("Resource " + path + " can't be loaded");
    }

    public static ConfigurationException propertyGetterFailed(String property) {
        return new ConfigurationException("Property getter " + property + " failed");
    }

}

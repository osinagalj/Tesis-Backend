package com.unicen.app.exceptions;

import lombok.Getter;

@Deprecated
@Getter
public class ObjectNotFoundException extends RuntimeException {

    private Class<?> objectClass;
    private Object id;
    private String property;

    public ObjectNotFoundException(Class<?> objectClass, Object id) {
        super("Object of type [" + objectClass + "] with id [" + id + "] not found");
        this.objectClass = objectClass;
        this.id = id;
    }

    public ObjectNotFoundException(Class<?> objectClass, String property, Object id) {
        super("Object of type [" + objectClass + "] with " + property + " [" + id + "] not found");
        this.objectClass = objectClass;
        this.property = property;
        this.id = id;
    }

}

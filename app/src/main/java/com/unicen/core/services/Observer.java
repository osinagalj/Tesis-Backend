package com.unicen.core.services;

public interface Observer<EventType, ObjectType> {

    void notify(EventType event, ObjectType object);

}

package com.unicen.app.services;

public interface Observer<EventType, ObjectType> {

    void notify(EventType event, ObjectType object);

}

package com.unicen.app.services;

import java.util.List;

public interface ObservableService<EventType, ObjectType> {

    void addObserver(Observer<EventType, ObjectType> observer);

    void removeObserver(Observer<EventType, ObjectType> observer);

    List<Observer<EventType, ObjectType>> getObservers();

    default void notifyAll(EventType event, ObjectType object) {
        getObservers().forEach(o -> o.notify(event, object));
    }

}

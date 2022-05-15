package com.unicen.app.services;

import com.unicen.app.model.LogEvent;
import com.unicen.app.repositories.LogEventRepository;
import org.springframework.stereotype.Service;

@Service
public class LogEventService extends PublicObjectCrudService<LogEvent, LogEventRepository> {

    public LogEventService(LogEventRepository repository) {
        super(repository);
    }

    @Override
    protected void updateData(LogEvent existingObject, LogEvent updatedObject) {
        existingObject.setContent(updatedObject.getContent());
        existingObject.setType(updatedObject.getType());
    }

    @Override
    protected Class<LogEvent> getObjectClass() {
        return LogEvent.class;
    }

}

package com.unicen.app.service;

import com.unicen.app.model.Image;
import com.unicen.app.model.Indicator;
import com.unicen.app.model.Metric;
import com.unicen.app.model.Result;
import com.unicen.app.repository.IndicatorRepository;
import com.unicen.app.repository.MetricRepository;
import com.unicen.app.repository.ResultRepository;
import com.unicen.core.services.PublicObjectCrudService;
import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;


@Service
public class IndicatorService extends PublicObjectCrudService<Indicator, IndicatorRepository> {
    private static Logger LOGGER = LoggerFactory.getLogger(GlobalApplicationContext.class);


    public IndicatorService(IndicatorRepository repository) {
        super(repository);

    }

    @Override
    protected void updateData(Indicator existingObject, Indicator updatedObject) {

    }

    @Override
    protected Class<Indicator> getObjectClass() {
        return Indicator.class;
    }


}

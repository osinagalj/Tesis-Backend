package com.unicen.app.service;

import com.unicen.app.model.Image;
import com.unicen.app.model.Metric;
import com.unicen.app.model.Result;
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
public class MetricService extends PublicObjectCrudService<Metric, MetricRepository> {
    private static Logger LOGGER = LoggerFactory.getLogger(GlobalApplicationContext.class);


    public MetricService(MetricRepository repository) {
        super(repository);

    }

    @Override
    protected void updateData(Metric existingObject, Metric updatedObject) {

    }

    @Override
    protected Class<Metric> getObjectClass() {
        return Metric.class;
    }



    @Transactional(readOnly = true)
    public Page<Metric> findPageImages(Long externalId, int page, int pageSize, Sort.Direction order, String... propertiesToOrder) {
        Sort sortBy = Sort.by(order, propertiesToOrder);
        Specification<Result> spec = (root, query, cb) -> cb.equal(root.get("originalImageId"), externalId);
        // Obtiene la primera página de imágenes del repositorio
        Page<Metric> imagePage = repository.findAll(spec, PageRequest.of(page, pageSize, sortBy));

        return imagePage;
    }

}

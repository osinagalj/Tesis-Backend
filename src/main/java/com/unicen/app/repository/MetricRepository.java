package com.unicen.app.repository;

import com.unicen.app.model.Metric;
import com.unicen.app.model.Result;
import com.unicen.core.repositories.PublicObjectRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricRepository extends PublicObjectRepository<Metric, Long> {
    Page<Metric> findAll(Specification<Result> spec, Pageable pageable);

}

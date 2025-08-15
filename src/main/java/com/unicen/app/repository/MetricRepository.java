package com.unicen.app.repository;

import com.unicen.app.model.Indicator;
import com.unicen.app.model.Metric;
import com.unicen.app.model.Result;
import com.unicen.core.repositories.PublicObjectRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetricRepository extends PublicObjectRepository<Metric, Long> {
    Page<Metric> findAll(Specification<Result> spec, Pageable pageable);

    @Query("SELECT m FROM Metric m WHERE m.originalImageId.id = :originalImageId AND m.ratio = :ratio")
    Optional<Metric> findByOriginalImageIdAndRatio(@Param("originalImageId") Long originalImageId, @Param("ratio") int ratio);
}

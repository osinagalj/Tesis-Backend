package com.unicen.app.repository;

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
public interface ResultRepository extends PublicObjectRepository<Result, Long> {
    @Query("Select i FROM Result i where i.externalId  = :externalId")
    Optional<Result> findByExternalIdAndFetchImageEagerly(@Param("externalId") String externalId);
    Page<Result> findAll(Specification<Result> spec, Pageable pageable);

    @Query("SELECT COUNT(i) FROM Result i where i.externalId  = :externalId")
    Integer getTotalElements(@Param("externalId") String externalId);
}

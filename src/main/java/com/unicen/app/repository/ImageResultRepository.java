package com.unicen.app.repository;

import com.unicen.app.model.ImageResult;
import com.unicen.core.repositories.PublicObjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageResultRepository extends PublicObjectRepository<ImageResult, Long> {
    @Query("Select i FROM ImageResult i where i.externalId  = :externalId")
    Optional<ImageResult> findByExternalIdAndFetchImageEagerly(@Param("externalId") String externalId);
    Page<ImageResult> findAll(Specification<ImageResult> spec,Pageable pageable);

    @Query("SELECT COUNT(i) FROM ImageResult i where i.externalId  = :externalId")
    Integer getTotalElements(@Param("externalId") String externalId);
}

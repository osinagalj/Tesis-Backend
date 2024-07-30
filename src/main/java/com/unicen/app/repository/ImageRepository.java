package com.unicen.app.repository;

import com.unicen.app.model.Image;
import com.unicen.core.repositories.PublicObjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends PublicObjectRepository<Image, Long> {
    Page<Image> findAll(Specification<Image> spec,Pageable pageable);

    @Query("Select i FROM Image i where i.externalId  = :externalId")
    Optional<Image> findByExternalIdAndFetchImageEagerly(@Param("externalId") String externalId);

}

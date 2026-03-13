package com.unicen.app.repository;

import com.unicen.app.model.Image;
import com.unicen.core.repositories.PublicObjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends PublicObjectRepository<Image, Long> {
    Page<Image> findAll(Specification<Image> spec,Pageable pageable);

    @Query("Select i FROM Image i where i.externalId  = :externalId")
    Optional<Image> findByExternalIdAndFetchImageEagerly(@Param("externalId") String externalId);

    @Query("SELECT COUNT(DISTINCT r.originalImageId) FROM Result r")
    long countImagesWithResults();

    @Query("SELECT COUNT(i) FROM Image i WHERE LOWER(i.type) IN :types")
    long countByType(@Param("types") java.util.List<String> types);

    @Query(value = "SELECT DATE(created_at), COUNT(*) FROM image WHERE created_at >= :startDate GROUP BY DATE(created_at)", nativeQuery = true)
    List<Object[]> countByDaySince(@Param("startDate") LocalDateTime startDate);

}

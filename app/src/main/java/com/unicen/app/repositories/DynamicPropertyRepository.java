package com.unicen.app.repositories;


import com.unicen.app.model.DynamicProperty;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository extending {@link CrudRepository} for {@link DynamicProperty}
 * Objects
 *
 * @author Sebastian Javier Guzman
 */
public interface DynamicPropertyRepository extends PagingAndSortingRepository<DynamicProperty, Long> {
    /**
     * Search for a Dynamic Property with the specified key value
     *
     * @param key the key value to search for
     * @return {@link Optional} content value
     */
    @Query(value = "FROM DynamicProperty WHERE key = :key")
    Optional<DynamicProperty> getValueForKey(@Param("key") String key);

    /**
     * Search for a Dynamic Property with the specified key value, that has been
     * active since the minimum release version provided.
     *
     * @param key     the key value to search for
     * @param version minimum version since when the key becomes active
     * @return {@link Optional} content value
     */
    @Query(value = "FROM DynamicProperty WHERE key = :key AND sinceVersion >= :version")
    Optional<DynamicProperty> getPropertyValueForVersion(@Param("key") String key, @Param("version") Long version);
}
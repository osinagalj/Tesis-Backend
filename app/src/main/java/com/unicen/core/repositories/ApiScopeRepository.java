package com.unicen.core.repositories;

import com.unicen.core.model.ApiScope;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ApiScopeRepository extends PublicObjectRepository<ApiScope, Long> {

    Optional<ApiScope> findByName(String name);

    @Transactional(readOnly = true)
    Set<ApiScope> findByNameIn(@Param("names") Collection<String> names);
}

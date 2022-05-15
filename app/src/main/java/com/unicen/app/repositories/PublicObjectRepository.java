package com.unicen.app.repositories;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface PublicObjectRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

    Optional<T> findByExternalId(String externalId);

    List<T> findByExternalIdIn(List<String> externalIds);
}

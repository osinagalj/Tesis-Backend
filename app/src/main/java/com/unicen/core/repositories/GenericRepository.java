package com.unicen.core.repositories;

import org.springframework.stereotype.Repository;

@Repository
public interface GenericRepository<T, ID> {
    T deleteById(ID id);
}

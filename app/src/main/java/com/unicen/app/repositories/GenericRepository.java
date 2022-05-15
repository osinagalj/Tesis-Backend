package com.unicen.app.repositories;

import org.springframework.stereotype.Repository;

@Repository
public interface GenericRepository<T, ID> {
    T deleteById(ID id);
}

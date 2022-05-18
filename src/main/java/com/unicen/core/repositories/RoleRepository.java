package com.unicen.core.repositories;

import com.unicen.core.model.AccessRole;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends PublicObjectRepository<AccessRole, Long> {

    public Optional<AccessRole> findByName(String name);
}

package com.unicen.app.repositories;


import com.unicen.app.model.AccessRole;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccessRoleRepository extends PublicObjectRepository<AccessRole, Long> {

    public Optional<AccessRole> findByName(String name);
}

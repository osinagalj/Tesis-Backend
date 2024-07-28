package com.ecofy.core.services;

import com.ecofy.core.exceptions.CoreApiException;
import com.ecofy.core.model.AccessRole;
import com.ecofy.core.repositories.AccessRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class AccessRoleService extends PublicObjectCrudService<AccessRole, AccessRoleRepository> {

    public AccessRoleService(AccessRoleRepository repository) {
        super(repository);
    }

    @Override
    protected void updateData(AccessRole existingObject, AccessRole updatedObject) {
        existingObject.setName(updatedObject.getName());
    }

    @Override
    protected Class<AccessRole> getObjectClass() {
        return AccessRole.class;
    }

    @Transactional
    public AccessRole ensure(String name) {
        return getByName(name, () -> save(name));
    }

    @Transactional
    public AccessRole save(String name) {
        return repository.save(new AccessRole(name));
    }

    @Transactional(readOnly = true)
    public AccessRole getByName(String name, Supplier<AccessRole> supplier) {
        return repository.findByName(name).orElseGet(supplier);
    }

    @Transactional(readOnly = true)
    public AccessRole getByName(String name) {
        return getByName(name, () -> {
            throw CoreApiException.objectNotFound("name: " + name);
        });
    }
}

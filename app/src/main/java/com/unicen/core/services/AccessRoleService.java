package com.unicen.core.services;

import com.unicen.core.exceptions.ObjectNotFoundException;
import com.unicen.core.model.AccessRole;
import com.unicen.core.repositories.AccessRoleRepository;
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
            throw new ObjectNotFoundException(AccessRole.class, "name", name);
        });
    }
}

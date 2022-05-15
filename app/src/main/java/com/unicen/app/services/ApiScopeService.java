package com.unicen.app.services;

import com.unicen.app.exceptions.CoreApiException;
import com.unicen.app.model.ApiScope;
import com.unicen.app.repositories.ApiScopeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class ApiScopeService extends PublicObjectCrudService<ApiScope, ApiScopeRepository> {

    public ApiScopeService(ApiScopeRepository repository) {
        super(repository);
    }

    @Override
    protected void updateData(ApiScope existingObject, ApiScope updatedObject) {
        existingObject.setName(updatedObject.getName());
    }

    @Override
    protected Class<ApiScope> getObjectClass() {
        return ApiScope.class;
    }

    @Transactional
    public ApiScope ensure(String name) {
        return getByName(name, () -> save(name));
    }

    @Transactional
    public ApiScope save(String name) {
        return repository.save(new ApiScope(name));
    }

    @Transactional(readOnly = true)
    public ApiScope getByName(String name, Supplier<ApiScope> supplier) {
        return repository.findByName(name).orElseGet(supplier);
    }

    public Set<ApiScope> getByNames(Collection<String> names) {
        return this.repository.findByNameIn(names);
    }

    @Transactional(readOnly = true)
    public ApiScope getByName(String name) {
        return getByName(name, () -> {
            throw CoreApiException.objectNotFound(ApiScope.class, "name", name);
        });
    }
}

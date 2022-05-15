package com.unicen.app.services;

import com.unicen.app.exceptions.ObjectNotFoundException;
import com.unicen.app.model.PublicModel;
import com.unicen.app.repositories.PublicObjectRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Specific type of CrudService that adds some methods to work with public
 * objects
 *
 * @param <Model>
 * @param <Repository>
 */
public abstract class PublicObjectCrudService<Model extends PublicModel, Repository extends PublicObjectRepository<Model, Long>>
        extends CrudService<Model, Repository> {

    public PublicObjectCrudService(Repository repository) {
        super(repository);
    }

    @Transactional
    public Model getByExternalId(String id) {
        return getByExternalId(id, () -> {
            throw new ObjectNotFoundException(getObjectClass(), "id", id);
        });
    }

    @Transactional
    public Model getByExternalId(String id, Supplier<Model> supplier) {
        return repository.findByExternalId(id).orElseGet(supplier);
    }

    public Optional<Model> findByExternalId(String id) {
        return repository.findByExternalId(id);
    }

    public List<Model> findByExternalIds(List<String> externalIds) {
        if (Objects.isNull(externalIds) || externalIds.isEmpty()) {
            return Collections.emptyList();
        }
        return repository.findByExternalIdIn(externalIds);
    }

    @Transactional
    public Model updateByExternalId(String id, Model updatedObject) {
        Model model = getByExternalId(id);
        updateData(model, updatedObject);
        return repository.save(model);
    }

    @Transactional
    public Model deleteByExternalId(String id) {
        Model object = getByExternalId(id);
        repository.delete(object);
        return object;
    }
}
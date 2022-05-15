package com.unicen.app.services;

import com.unicen.app.dto.AuthenticatedUserDTO;
import com.unicen.app.exceptions.ObjectNotFoundException;
import com.unicen.app.model.PublicModel;
import com.unicen.app.model.UserBoundedObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Generic service that implements the traditional CRUD operations
 *
 * @param <Model>      The type of objects to be stored, updated, listed and
 *                     deleted
 * @param <Repository> The repository of {@code Model} that will be used to
 *                     store, update, list and delete the objects
 */
public abstract class CrudService<Model, Repository extends PagingAndSortingRepository<Model, Long>> {

    protected Repository repository;

    public CrudService(Repository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Model> findAll() {
        Iterable<Model> result = this.repository.findAll();
        return StreamSupport.stream(result.spliterator(), false).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Model getById(long id) {
        return getById(id, () -> {
            throw new ObjectNotFoundException(getObjectClass(), id);
        });
    }

    @Transactional(readOnly = true)
    public Model getById(long id, Supplier<? extends Model> supplier) {
        return repository.findById(id).orElseGet(supplier);
    }

    @Transactional(readOnly = true)
    public Page<Model> findPage(int page, int pageSize) {
        return repository.findAll(PageRequest.of(page, pageSize));
    }

    @Transactional(readOnly = true)
    public Page<Model> findPage(int page, int pageSize, Sort.Direction order, String... propertiesToOrder) {
        Sort sortBy = Sort.by(order, propertiesToOrder);
        return repository.findAll(PageRequest.of(page, pageSize, sortBy));
    }

    @Transactional()
    public Model save(Model model, AuthenticatedUserDTO authenticatedUser) {
        enrichObject(model, authenticatedUser);
        return save(model);
    }

    @Transactional()
    public Model save(Model model) {
        enrichObject(model, null);
        return repository.save(model);
    }

    public void enrichObject(Model model, AuthenticatedUserDTO authenticatedUser) {
        if (model instanceof PublicModel) {
            ((PublicModel) model).ensureExternalId();
        }

    }

    @Transactional()
    public Model update(Long id, Model updatedObject) {
        Model object = getById(id);
        updateData(object, updatedObject);
        return save(object);
    }

    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    /**
     * This method specifies how a model object should be updated from a DTO
     *
     * It should return the object with its data updated
     *
     * @param existingObject model object to be updated
     * @param updatedObject  updated object that has the information to be updated
     * @return updated model object
     */
    protected abstract void updateData(Model existingObject, Model updatedObject);

    @Transactional
    public void delete(long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void delete(Model model) {
        repository.delete(model);
    }

    protected abstract Class<Model> getObjectClass();

}
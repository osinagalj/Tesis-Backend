package com.unicen.core.services;


import com.unicen.core.dto.DynamicPropertyCreationDTO;
import com.unicen.core.model.DynamicProperty;
import com.unicen.core.repositories.DynamicPropertyRepository;
import org.springframework.stereotype.Service;

/**
 * {@link DynamicProperty}'s Service
 *
 * @author Sebastian Javier Guzman
 */
@Service
public class DynamicPropertyService extends CrudService<DynamicProperty, DynamicPropertyRepository> {

    public DynamicPropertyService(DynamicPropertyRepository repository) {
        super(repository);
    }

    @Override
    protected Class<DynamicProperty> getObjectClass() {
        return DynamicProperty.class;
    }

    @Override
    protected void updateData(DynamicProperty dynamicProperty, DynamicProperty dynamicPropertyDTO) {
        dynamicProperty.setContent(dynamicPropertyDTO.getContent());
        dynamicProperty.setSinceVersion(dynamicPropertyDTO.getSinceVersion());
    }

    /**
     * Creates a DynamicProperty Object and saves it on its repository.
     *
     *                                   Information
     * @return {@link DynamicProperty} newly created Dynamic Property
     */
    public DynamicProperty create(DynamicPropertyCreationDTO dynamicPropertyCreationDTO) {
        return this.repository.save(new DynamicProperty(dynamicPropertyCreationDTO.getKey(), dynamicPropertyCreationDTO.getContent(),
                dynamicPropertyCreationDTO.getSinceVersion()));
    }

    /**
     * Search for a Dynamic Property with the key value provided. If there's no
     * coincidence the given default value is returned.
     *
     * @param key          key value to search for
     * @param defaultValue value returned if there's no match
     * @return {@link String} content of the key, or default value
     */
    public String getValueForKey(String key, String defaultValue) {
        return repository.getValueForKey(key).orElseGet(() -> new DynamicProperty(key, defaultValue, 1L)).getContent();
    }

    /**
     * Searches for a Dynamic Property with the given key value, and a release
     * version equal or bigger than the sinceVersion value provided.
     *
     * @param key          key value to search for
     * @param defaultValue value returned if there's no match
     * @param version      minimum version of release
     * @return {@link String} content of the key or default value
     */
    public String getPropertyValueForVersion(String key, String defaultValue, Long version) {
        return repository.getPropertyValueForVersion(key, version).orElseGet(() -> new DynamicProperty(key, defaultValue, 1L)).getContent();
    }
}
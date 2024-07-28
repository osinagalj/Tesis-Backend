package com.ecofy.core.dto.utils;

import com.ecofy.core.model.DTO;
import org.modelmapper.ModelMapper;

/**
 * Wrapper class to be used to quickly perform DTO-instance mappings within the code
 *
 * A mechanism for refining DTO <-> Model mappings will be defined for specific cases
 *
 */
public class DTOMapper {

    private ModelMapper mapper;
    private static DTOMapper instance;

    public DTOMapper(ModelMapper mapper) {
        this.mapper = mapper;

        // ensure that we manage only one singleton instance
        DTOMapper.instance = this;
    }

    public static DTOMapper get() {
        return instance;
    }

    public <T1, T2> T2 map(T1 object, Class<T2> destClass) {
        T2 destObject = mapper.map(object, destClass);

        // if the destination object is a DTO, then invoke post-construct method so each one
        // can customize which actions need to be done post conversion...
        if (destObject instanceof DTO) {
            ((DTO) destObject).postConstructFrom(object);
        }

        // ... on the other hand, if the original object is a DTO, then call the `afterGenerating` method if any
        // tweak needs to be done over the destination model object
        if (object instanceof DTO) {
            ((DTO) object).afterGenerating(destObject);
        }

        return destObject;
    }
}

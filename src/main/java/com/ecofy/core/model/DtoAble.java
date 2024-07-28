package com.ecofy.core.model;

import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public interface DtoAble<DTO> {

    ModelMapper mm = new ModelMapper();

    @SneakyThrows
    default DTO initWith(Object model) {
        return (DTO) mm.map(model, getClass());
    }

    @SneakyThrows
    default <T> T as(Class<T> clazz) {
        mm.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return (T) mm.map(this, clazz);
    }

    @SneakyThrows
    default <T> T to(T model) {
        return model;
    }

}
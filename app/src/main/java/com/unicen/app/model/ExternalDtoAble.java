package com.unicen.app.model;

import lombok.SneakyThrows;

public interface ExternalDtoAble<DTO> extends DtoAble<DTO> {

    @SneakyThrows
    default <T> T as(Class<T> clazz) {
        T target = DtoAble.super.as(clazz);
        ((PublicModel) target).ensureExternalId();
        return target;
    }

    @SneakyThrows
    default <T extends PublicModel> T to(T clazz) {
        T target = DtoAble.super.to(clazz);
        target.ensureExternalId();
        return target;
    }
}
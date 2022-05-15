package com.unicen.app.controller;

import com.unicen.app.dto.ApiResultDTO;
import com.unicen.app.model.DtoAble;
import com.unicen.app.model.GenericSuccessResponse;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericController<Model, ModelDTO extends DtoAble> {

    protected ResponseEntity<ApiResultDTO<List<ModelDTO>>> listResult(List<Model> objects) {
        return this.genericListResult(objects, this.getDTOClass());
    }

    protected ResponseEntity<ApiResultDTO<Page<ModelDTO>>> pageResult(Page<Model> page) {
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(page.map(o -> {
            try {
                return getDTOClass().cast(getDTOClass().getConstructor().newInstance().initWith(o));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })));
    }

    protected <TModel, MDTO extends DtoAble> ResponseEntity<ApiResultDTO<List<MDTO>>> genericListResult(List<TModel> objects, Class<MDTO> dtoClass) {
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(objects.stream().map(o -> {
            try {
                return modelToDto(dtoClass, o);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList())));
    }

    protected ResponseEntity<ApiResultDTO<ModelDTO>> uniqueResult(Model optionalObject) {
        return genericUniqueResult(optionalObject, this.getDTOClass());
    }

    protected <TModel, TDto extends DtoAble> ResponseEntity<ApiResultDTO<TDto>> genericUniqueResult(TModel optionalObject, Class<TDto> dtoClass) {
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(modelToDto(dtoClass, optionalObject)));
    }

    protected Model dtoToModel(ModelDTO dto) {
        return getObjectClass().cast(dto.as(getObjectClass()));
    }

    @SneakyThrows
    protected <TModel, MDTO extends DtoAble> MDTO modelToDto(Class<MDTO> dtoClass, TModel o) {
        return dtoClass.cast(dtoClass.getConstructor().newInstance().initWith(o));
    }

    protected abstract Class<ModelDTO> getDTOClass();

    protected abstract Class<Model> getObjectClass();

    protected ResponseEntity<ApiResultDTO<GenericSuccessResponse>> ok() {
        return ResponseEntity.ok(ApiResultDTO.ofSuccess(new GenericSuccessResponse()));
    }
}

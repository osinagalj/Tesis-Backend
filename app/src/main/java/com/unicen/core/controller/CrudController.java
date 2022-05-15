package com.unicen.core.controller;

import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.dto.AuthenticatedUserDTO;
import com.unicen.core.model.DtoAble;
import com.unicen.core.model.GenericSuccessResponse;
import com.unicen.core.model.PublicModel;
import com.unicen.core.repositories.PublicObjectRepository;
import com.unicen.core.security.GenericAuthenticationToken;
import com.unicen.core.services.PublicObjectCrudService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@PreAuthorize("hasAnyRole('ROLE_SUPPORT', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
public abstract class CrudController<Model extends PublicModel, ModelDTO extends DtoAble, Service extends PublicObjectCrudService<Model, ? extends PublicObjectRepository<Model, Long>>> {

    @Autowired protected Service service;

    @PreAuthorize("hasAnyRole('ROLE_SUPERADMIN')")
    @GetMapping
    public ResponseEntity<ApiResultDTO<Page<ModelDTO>>> read(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return pageResult(service.findPage(page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResultDTO<ModelDTO>> getById(@PathVariable("id") String id) {
        return uniqueResult(service.getByExternalId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResultDTO<ModelDTO>> update(@PathVariable("id") String id, @RequestBody ModelDTO dto) {
        return uniqueResult(service.updateByExternalId(id, dtoToModel(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResultDTO<ModelDTO>> delete(@PathVariable("id") String id) {
        return uniqueResult(service.deleteByExternalId(id));
    }

    @PostMapping()
    public ResponseEntity<ApiResultDTO<ModelDTO>> create(@RequestBody ModelDTO dto) {
        GenericAuthenticationToken authentication = (GenericAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return uniqueResult(service.save(dtoToModel(dto)));
        } else {
            return uniqueResult(service.save(dtoToModel(dto), new AuthenticatedUserDTO(authentication.getUsername())));
        }
    }


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
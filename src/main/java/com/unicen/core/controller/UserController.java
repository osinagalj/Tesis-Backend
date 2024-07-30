package com.unicen.core.controller;


import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.dto.UserModelDTO;
import com.unicen.core.model.User;
import com.unicen.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERADMIN')")
@RequestMapping("/users")
public class UserController extends GenericController<User, UserModelDTO> {

    @Override
    protected Class<UserModelDTO> getDTOClass() {
        return UserModelDTO.class;
    }

    @Override
    protected Class<User> getObjectClass() {
        return User.class;
    }

    @Autowired
    protected UserService service;

    @GetMapping
    public ResponseEntity<ApiResultDTO<Page<UserModelDTO>>> read(@RequestParam(defaultValue = "0") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return pageResult(service.findPage(page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResultDTO<UserModelDTO>> getById(@PathVariable("id") String id) {
        return uniqueResult(service.getByExternalId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResultDTO<UserModelDTO>> update(@PathVariable("id") String id, @RequestBody UserModelDTO dto) {
        return uniqueResult(service.updateByExternalId(id, dtoToModel(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResultDTO<UserModelDTO>> delete(@PathVariable("id") String id) {
        return uniqueResult(service.deleteByExternalId(id));
    }

    @PostMapping()
    public ResponseEntity<ApiResultDTO<UserModelDTO>> create(@RequestBody UserModelDTO dto) {
        return uniqueResult(service.save(dtoToModel(dto)));
    }

}

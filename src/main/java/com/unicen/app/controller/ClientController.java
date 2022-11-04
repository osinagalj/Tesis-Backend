package com.unicen.app.controller;

import com.unicen.core.controller.GenericController;
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
@RequestMapping("/register")
public class ClientController extends GenericController<User, UserModelDTO> {

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

    @PostMapping()
    public ResponseEntity<ApiResultDTO<UserModelDTO>> create(@RequestBody UserModelDTO dto) {
        return uniqueResult(service.save(dtoToModel(dto)));
    }

}
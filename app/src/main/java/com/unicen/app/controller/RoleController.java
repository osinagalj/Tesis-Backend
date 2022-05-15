package com.unicen.app.controller;


import com.unicen.app.dto.AccessRoleDTO;
import com.unicen.app.model.AccessRole;
import com.unicen.app.services.AccessRoleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/accessroles")
public class RoleController extends CrudController<AccessRole, AccessRoleDTO, AccessRoleService> {

    @Override
    protected Class<AccessRoleDTO> getDTOClass() {
        return AccessRoleDTO.class;
    }

    @Override
    protected Class<AccessRole> getObjectClass() {
        return AccessRole.class;
    }
}

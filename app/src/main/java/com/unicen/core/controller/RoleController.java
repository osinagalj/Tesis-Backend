package com.unicen.core.controller;


import com.unicen.core.dto.AccessRoleDTO;
import com.unicen.core.model.AccessRole;
import com.unicen.core.services.AccessRoleService;
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

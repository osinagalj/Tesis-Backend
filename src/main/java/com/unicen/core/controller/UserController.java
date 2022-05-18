package com.unicen.core.controller;


import com.unicen.core.dto.UserModelDTO;
import com.unicen.core.model.User;
import com.unicen.core.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController extends CrudController<User, UserModelDTO, UserService> {

    @Override
    protected Class<UserModelDTO> getDTOClass() {
        return UserModelDTO.class;
    }

    @Override
    protected Class<User> getObjectClass() {
        return User.class;
    }

}

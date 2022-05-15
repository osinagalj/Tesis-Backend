package com.unicen.app.controller;


import com.unicen.app.dto.UserModelDTO;
import com.unicen.app.model.User;
import com.unicen.app.services.UserService;
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

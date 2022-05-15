package com.unicen.app.controller;


import com.unicen.app.configuration.ApplicationPropertiesService;
import com.unicen.app.dto.ApiResultDTO;
import com.unicen.app.dto.UserModelDTO;
import com.unicen.app.exceptions.CoreApiException;
import com.unicen.app.model.AddRoleRequestDTO;
import com.unicen.app.model.SignUpRequestDTO;
import com.unicen.app.model.User;
import com.unicen.app.model.UserStatusRequestDTO;
import com.unicen.app.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

//todo remove
@Controller
@RequestMapping("/admin")
public class AdminController extends GenericController<User, UserModelDTO> {

    private final ApplicationPropertiesService propertiesService;
    private UserService userService;

    public AdminController(UserService userService, ApplicationPropertiesService propertiesService) {
        this.userService = userService;
        this.propertiesService = propertiesService;
    }

    @PostMapping("/public/admins")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResultDTO<UserModelDTO>> createAdmin(@RequestHeader("x-otas") String secret, @RequestBody SignUpRequestDTO dto) {
        validateSecret(secret);

        return uniqueResult(userService.createAdmin(dto.getFirstName(), dto.getLastName(), dto.getEmail(), dto.getPassword()));
    }

    private void validateSecret(String secret) {
        checkAdminEndpointsEnabled();
        userService.validateOneTimeAdminSecret(secret);
    }

    @PostMapping("/public/roles")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResultDTO<UserModelDTO>> createAdmin(@RequestHeader("x-otas") String secret, @RequestBody AddRoleRequestDTO dto) {
        validateSecret(secret);

        return uniqueResult(userService.addRole(dto.getEmail(), dto.getRoleName()));
    }

    @PostMapping("/public/remove-role")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResultDTO<UserModelDTO>> removeRoles(@RequestHeader("x-otas") String secret, @RequestBody AddRoleRequestDTO dto) {
        validateSecret(secret);

        return uniqueResult(userService.removeRole(dto.getEmail(), dto.getRoleName()));
    }

    @PostMapping("/public/user-status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResultDTO<UserModelDTO>> setUserStatus(@RequestHeader("x-otas") String secret, @RequestBody UserStatusRequestDTO dto) {
        validateSecret(secret);

        return uniqueResult(userService.setEnabledStatus(dto.getEmail(), dto.isEnabled()));
    }

    private void checkAdminEndpointsEnabled() {
        if (!this.propertiesService.adminEndpointsEnabled()) {
            throw CoreApiException.insufficientPermissions();
        }
    }

    @Override
    protected Class<UserModelDTO> getDTOClass() {
        return UserModelDTO.class;
    }

    @Override
    protected Class<User> getObjectClass() {
        return User.class;
    }

}

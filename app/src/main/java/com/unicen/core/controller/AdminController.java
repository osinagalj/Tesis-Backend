package com.unicen.core.controller;


import com.unicen.core.configuration.ApplicationPropertiesService;
import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.dto.UserModelDTO;
import com.unicen.core.exceptions.CoreApiException;
import com.unicen.core.model.AddRoleRequestDTO;
import com.unicen.core.model.SignUpRequestDTO;
import com.unicen.core.model.User;
import com.unicen.core.model.UserStatusRequestDTO;
import com.unicen.core.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;


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

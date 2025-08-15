package com.unicen.app.controller;

import com.unicen.app.dto.ProfileDTO;
import com.unicen.app.model.ImageType;
import com.unicen.core.controller.GenericController;
import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.model.GenericSuccessResponse;
import com.unicen.core.model.User;
import com.unicen.core.security.GenericAuthenticationToken;
import com.unicen.core.services.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
@PreAuthorize("permitAll")
@Api(tags = "6. Profile")
/*@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")*/
public class ProfileController extends GenericController<User, ProfileDTO> {

    @Override
    protected Class<ProfileDTO> getDTOClass() {
        return ProfileDTO.class;
    }

    @Override
    protected Class<User> getObjectClass() {
        return User.class;
    }

    @Autowired
    protected UserService service;

    @GetMapping()
    public ResponseEntity<ApiResultDTO<ProfileDTO>> getProfile() {
        GenericAuthenticationToken authentication = (GenericAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return uniqueResult(service.getById((long)authentication.getPrincipal()));
    }

    @PutMapping
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> update(@RequestBody ProfileDTO dto) {
        GenericAuthenticationToken authentication = (GenericAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        service.updateProfileData(dto, authentication.getUsername());
        return ok();
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> uploadImage(@RequestParam("userExternalId") String userExternalId, @RequestParam("type") String type, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        service.updatePictureOfUser(userExternalId, ImageType.getType(type), multipartFile);
        return ok();
    }



}
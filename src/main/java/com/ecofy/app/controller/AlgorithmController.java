package com.ecofy.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecofy.app.dto.ImageResultDTO;
import com.ecofy.app.model.Algorithm;
import com.ecofy.app.model.ImageResult;
import com.ecofy.app.service.AlgorithmService;
import com.ecofy.core.controller.GenericController;
import com.ecofy.core.dto.ApiResultDTO;
import com.ecofy.core.model.GenericSuccessResponse;
import com.ecofy.core.model.User;
import com.ecofy.core.security.GenericAuthenticationToken;
import com.ecofy.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@PreAuthorize("permitAll")
@RequestMapping("/algorithm")
public class AlgorithmController extends GenericController<ImageResult, ImageResultDTO> {

    @Override
    protected Class<ImageResultDTO> getDTOClass() {
        return ImageResultDTO.class;
    }

    @Override
    protected Class<ImageResult> getObjectClass() {
        return ImageResult.class;
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    AlgorithmService algorithmService;

    @Autowired
    UserService userService;

    @PostMapping("/process")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> uploadImage(@RequestParam("resourceExternalId") String resourceExternalId,  @RequestParam("algorithm") Algorithm algorithm, @RequestParam("ratioFrom") Integer ratioFrom, @RequestParam("ratioTo") Integer ratioTo) throws IOException {
        GenericAuthenticationToken authentication = (GenericAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getById((long)authentication.getPrincipal());
        algorithmService.process(algorithm, resourceExternalId, ratioFrom, ratioTo, user);
        return ok();
    }


}

package com.unicen.app.controller;

import com.unicen.app.dto.ImageResultDTO;
import com.unicen.app.model.Algorithm;
import com.unicen.app.model.Result;
import com.unicen.app.service.AlgorithmService;
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

import java.io.IOException;

@Controller
@PreAuthorize("permitAll")
@Api(tags = "3. Algorithms")
@RequestMapping("/algorithm")
public class AlgorithmController extends GenericController<Result, ImageResultDTO> {

    @Override
    protected Class<ImageResultDTO> getDTOClass() {
        return ImageResultDTO.class;
    }

    @Override
    protected Class<Result> getObjectClass() {
        return Result.class;
    }

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

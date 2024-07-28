package com.ecofy.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecofy.app.dto.ImageResultDTO;
import com.ecofy.app.model.Image;
import com.ecofy.app.model.ImageResult;
import com.ecofy.app.service.ImageResultService;
import com.ecofy.app.service.ImageService;
import com.ecofy.core.controller.GenericController;
import com.ecofy.core.dto.ApiResultDTO;
import com.ecofy.core.exceptions.CoreApiException;
import com.ecofy.core.model.User;
import com.ecofy.core.security.GenericAuthenticationToken;
import com.ecofy.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@PreAuthorize("permitAll")
@RequestMapping("/results")
public class ImageResultController extends GenericController<ImageResult, ImageResultDTO> {

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
    ImageResultService service;

    @Autowired
    UserService userService;

    @Autowired
    ImageService imageService;

    @GetMapping("/getAll")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<Page<ImageResultDTO>>> getImagesForUser( @RequestParam("imageExternalId") String imageExternalId, @RequestParam(defaultValue = "0") Integer page,
                                                                          @RequestParam(defaultValue = "5") Integer pageSize) throws IOException {
        GenericAuthenticationToken authentication = (GenericAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getById((long)authentication.getPrincipal());
        Image image = imageService.getByExternalId(imageExternalId);
        return pageResult(service.findPageImages(image.getId(), page, pageSize, Sort.Direction.DESC, "createdAt"));

    }

    @GetMapping("/getResource")
    @ResponseBody
    public ResponseEntity<Resource> getImageResource(@RequestParam("externalId") String externalId) throws IOException {
        //working
        MediaType contentType = MediaType.IMAGE_JPEG;
        InputStream in = getClass().getResourceAsStream("/static/messi.jpg");

        //var image = service.findAll().stream().findFirst();
        var image =  service.findByExternalIdAndFetchImageEagerly(externalId);


        // var a = service.findAll().stream().findFirst();
        if(!image.isPresent()){
            throw CoreApiException.objectNotFound("Resource : " + externalId + " not exists");

        }
        InputStream is = new ByteArrayInputStream(image.get().getImageData());

        return ResponseEntity.ok()
                .contentType(contentType)
                        .body(new InputStreamResource(is));
    }



}

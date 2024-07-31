package com.unicen.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicen.app.dto.ImageResultDTO;
import com.unicen.app.model.Image;
import com.unicen.app.model.ImageResult;
import com.unicen.app.service.ImageResultService;
import com.unicen.app.service.ImageService;
import com.unicen.core.controller.GenericController;
import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.exceptions.CoreApiException;
import com.unicen.core.model.User;
import com.unicen.core.security.GenericAuthenticationToken;
import com.unicen.core.services.UserService;
import io.swagger.annotations.Api;
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
@Api(tags = "6. Results")
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

        var image =  service.findByExternalIdAndFetchImageEagerly(externalId);

        if(!image.isPresent()){
            throw CoreApiException.objectNotFound("Resource : " + externalId + " not exists");

        }
        InputStream is = new ByteArrayInputStream(image.get().getImageData());

        return ResponseEntity.ok()
                .contentType(contentType)
                        .body(new InputStreamResource(is));
    }



}

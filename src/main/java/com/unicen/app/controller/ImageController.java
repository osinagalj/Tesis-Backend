package com.unicen.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicen.app.dto.ImageDTO;
import com.unicen.app.model.Image;
import com.unicen.app.model.ImageType;
import com.unicen.app.service.ImageService;
import com.unicen.core.controller.GenericController;
import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.exceptions.CoreApiException;
import com.unicen.core.model.GenericSuccessResponse;
import com.unicen.core.model.User;
import com.unicen.core.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.springframework.core.io.Resource;

@Controller
@PreAuthorize("permitAll")
@RequestMapping("/images")
public class ImageController extends GenericController<Image, ImageDTO> {

    @Override
    protected Class<ImageDTO> getDTOClass() {
        return ImageDTO.class;
    }

    @Override
    protected Class<Image> getObjectClass() {
        return Image.class;
    }

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    ImageService service;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<ApiResultDTO<Page<ImageDTO>>> read(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return pageResult(service.findPage(page, pageSize, Sort.Direction.ASC, "createdAt"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResultDTO<ImageDTO>> getById(@PathVariable("id") String id) {
        return uniqueResult(service.getByExternalId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResultDTO<ImageDTO>> update(@PathVariable("id") String id, @RequestBody ImageDTO dto) {
        return uniqueResult(service.updateByExternalId(id, dtoToModel(dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResultDTO<ImageDTO>> delete(@PathVariable("id") String id) {
        return uniqueResult(service.deleteByExternalId(id));
    }

    @PostMapping()
    public ResponseEntity<ApiResultDTO<ImageDTO>> create(@RequestBody ImageDTO dto) {
        return uniqueResult(service.save(dtoToModel(dto)));
    }


    @GetMapping("/getimage")
    @ResponseBody
    public ResponseEntity<Resource> getImageDynamicType(@RequestParam("userExternalId") String userExternalId) throws IOException {
        //working
        MediaType contentType = MediaType.IMAGE_JPEG;
        InputStream in = getClass().getResourceAsStream("/static/messi.jpg");

        var user = userService.findByExternalIdAndFetchImageEagerly(userExternalId);
        // var a = service.findAll().stream().findFirst();

        if (user.isPresent()) {
            if (user.get().getImage() != null) {
                InputStream is = new ByteArrayInputStream(user.get().getImage().getImageData());

                return ResponseEntity.ok()
                        .contentType(contentType)
                        .body(new InputStreamResource(is));
            } else {
                throw CoreApiException.objectNotFound("Picture for user: " + user.get().getEmail() + "hasn't been set");
            }
        } else {
            throw CoreApiException.objectNotFound("User: " + user.get().getEmail() + "not exists");
        }
    }

    @GetMapping("/getCustomImages")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<Page<ImageDTO>>> getImagesForUser(@RequestParam("userExternalId") String userExternalId, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "5") Integer pageSize) throws IOException {
        var user = userService.findByExternalId(userExternalId);
        if (user.isPresent()) {
            return pageResult(service.findPageImages(page, pageSize, user.get(), Sort.Direction.DESC, "createdAt"));
        } else {
            throw CoreApiException.objectNotFound("User: " + user.get().getEmail() + "not exists");
        }
    }


    @GetMapping("/getResource")
    @ResponseBody
    public ResponseEntity<Resource> getImageResource(@RequestParam("externalId") String externalId) throws IOException {
        //working
        MediaType contentType = MediaType.IMAGE_JPEG;
        InputStream in = getClass().getResourceAsStream("/static/messi.jpg");

        var image = service.findByExternalIdAndFetchImageEagerly(externalId);
        // var a = service.findAll().stream().findFirst();
        if (!image.isPresent()) {
            throw CoreApiException.objectNotFound("Resource : " + externalId + " not exists");

        }
        InputStream is = new ByteArrayInputStream(image.get().getImageData());

        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new InputStreamResource(is));
    }

    @PostMapping("/upload-image")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> uploadImage(@RequestParam("userExternalId") String userExternalId, @RequestParam("type") String type, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        //TODO change this
        Optional<User> user = userService.findByExternalId(userExternalId); //repository.findByExternalId(userExternalId);
        var a = ImageType.getType(type);
        if (user.isPresent()) {
            service.saveImage(user.get(), ImageType.getType(type), multipartFile);
            return null;
        }
        return ok();
    }

}

package com.unicen.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicen.app.dto.ImageDTO;
import com.unicen.app.dto.Json;
import com.unicen.app.model.Image;
import com.unicen.app.service.ImageService;
import com.unicen.core.controller.GenericController;
import com.unicen.core.dto.ApiResultDTO;
import com.unicen.core.model.GenericSuccessResponse;
import com.unicen.core.model.User;
import com.unicen.core.services.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return pageResult(service.findPage(page, pageSize, Sort.Direction.DESC, "createdAt"));
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


    @PostMapping("/upload3")
    public ResponseEntity<ApiResultDTO<ImageDTO>> uploadImage3(@RequestParam String userExternalId) throws IOException {

        return uniqueResult(new Image("A",1,11,"U","T","D",null, null));
    }

    @PostMapping("/upload2")
    public ResponseEntity<ApiResultDTO<ImageDTO>> uploadImage2(@RequestParam("userExternalId") String userExternalId, @RequestParam("file") MultipartFile multipartFile)  {

        return uniqueResult(new Image("A",1,11,"U","T","D",null, null));
    }

    @GetMapping("/getimage")
    @ResponseBody
    public ResponseEntity<Resource> getImageDynamicType( @RequestParam("userExternalId") String userExternalId) throws IOException {
        //working
        MediaType contentType = MediaType.IMAGE_JPEG ;
        InputStream in = getClass().getResourceAsStream("/static/messi.jpg");

       var user =  userService.findByExternalIdAndFetchImageEagerly(userExternalId);
       // var a = service.findAll().stream().findFirst();

       if(user.isPresent()){
           if(user.get().getImage() != null){
               InputStream is = new ByteArrayInputStream(user.get().getImage().getImageData());

               return ResponseEntity.ok()
                       .contentType(contentType)
                       .body(new InputStreamResource(is));
           }
       }
        return ResponseEntity.ok()
                .contentType(contentType)
                .body(new InputStreamResource(in));
    }


/*
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> uploadImage( @RequestParam("userExternalId") String userExternalId, @RequestParam("file") MultipartFile multipartFile) throws IOException {

        return ok();
    }
*/

/*    @PostMapping("/upload")
    public ResponseEntity<ApiResultDTO<GenericSuccessResponse>> uploadImage( @RequestParam("file") MultipartFile multipartFile) throws IOException {
        service.saveImage(null, multipartFile);
        return ok();
    }*/
}

package com.unicen.app.controller;

import com.unicen.app.dto.ImageDTO;
import com.unicen.app.model.Image;
import com.unicen.app.service.ImageService;
import com.unicen.core.controller.GenericController;
import com.unicen.core.dto.ApiResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    ImageService service;

    @GetMapping
    public ResponseEntity<ApiResultDTO<Page<ImageDTO>>> read(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return pageResult(service.findPage(page, pageSize));
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

}

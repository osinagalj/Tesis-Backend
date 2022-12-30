package com.unicen.app.service;

import com.unicen.app.dto.ImageDTO;
import com.unicen.app.model.Image;
import com.unicen.app.repository.ImageRepository;
import com.unicen.core.model.User;
import com.unicen.core.services.CrudService;

import com.unicen.core.services.EntitiesDrawer;
import com.unicen.core.services.PublicObjectCrudService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class ImageService extends PublicObjectCrudService<Image, ImageRepository> {

    public ImageService(ImageRepository repository) {
        super(repository);
    }

    @Override
    protected void updateData(Image existingObject, Image updatedObject) {

    }

    @Override
    protected Class<Image> getObjectClass() {
        return Image.class;
    }




    public Image saveImage(ImageDTO dto, MultipartFile file) throws IOException {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Image image = new Image(fileName,1,1,"a","a,","d",null, file.getBytes());
        image.ensureExternalId();

 /*               .width(1)
                .description("test")
                .url("test.com")
                .height(2)
                .imageData(file.getBytes())
                .name(fileName)
                .build();*/


               // (fileName, file.getContentType(), file.getBytes());

        return repository.save(image);
    }
}

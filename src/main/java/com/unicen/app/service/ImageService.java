package com.unicen.app.service;

import com.unicen.app.model.Image;
import com.unicen.app.repository.ImageRepository;
import com.unicen.core.services.CrudService;

import com.unicen.core.services.PublicObjectCrudService;
import org.springframework.stereotype.Service;


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

}

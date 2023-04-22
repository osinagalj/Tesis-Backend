package com.unicen.app.service;

import com.unicen.app.dto.ImageDTO;
import com.unicen.app.model.Image;
import com.unicen.app.repository.ImageRepository;
import com.unicen.core.model.User;
import com.unicen.core.services.CrudService;

import com.unicen.core.services.EntitiesDrawer;
import com.unicen.core.services.PublicObjectCrudService;
import com.unicen.core.services.UserService;
import org.hibernate.cfg.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;


@Service
public class ImageService extends PublicObjectCrudService<Image, ImageRepository> {

/*
    private final UserService userService;
*/
    public ImageService(ImageRepository repository) {
        super(repository);
/*
        this.userService = userService;
*/
    }

    @Override
    protected void updateData(Image existingObject, Image updatedObject) {

    }

    @Override
    protected Class<Image> getObjectClass() {
        return Image.class;
    }


    @Transactional
    public void saveImage(User user, String type, MultipartFile file) throws IOException {


        Image image = new Image("Image from test 2",1,11,"U",type,"D",user,file.getBytes(), user);
        image.ensureExternalId();
        repository.save(image);
        /*user.setImage(imageService.save(image));*/
        /*this.update(user.getId(), user);*/
        System.out.println("finish updating user pict..");
    }

    @Transactional(readOnly = true)
    public Page<Image> findPageImages(int page, int pageSize,User user, Sort.Direction order, String... propertiesToOrder) {
        Sort sortBy = Sort.by("createdAt").ascending();
        Specification<Image> spec = (root, query, cb) -> cb.equal(root.get("owner"), user.getId());
        // Obtiene la primera página de imágenes del repositorio
        Page<Image> imagePage = repository.findAll(spec, PageRequest.of(page, pageSize, sortBy));
        return imagePage;
    }


    @Transactional
    public Optional<Image> findByExternalIdAndFetchImageEagerly(String externalId) throws IOException {
        Specification<Image> spec = (root, query, cb) -> cb.equal(root.get("externalId"), externalId);
        // Obtiene la primera página de imágenes del repositorio
        var imagePage = repository.findByExternalId(externalId);
        return  repository.findByExternalIdAndFetchImageEagerly(externalId);   //new ArrayList<>();
    }


}

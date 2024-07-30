package com.unicen.app.service;

import com.unicen.app.model.Image;
import com.unicen.app.model.ImageResult;
import com.unicen.app.repository.ImageResultRepository;
import com.unicen.core.services.PublicObjectCrudService;
import com.unicen.core.spring.lightweightcontainer.GlobalApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;


@Service
public class ImageResultService extends PublicObjectCrudService<ImageResult, ImageResultRepository> {
    private static Logger LOGGER = LoggerFactory.getLogger(GlobalApplicationContext.class);


    public ImageResultService(ImageResultRepository repository) {
        super(repository);

    }

    @Override
    protected void updateData(ImageResult existingObject, ImageResult updatedObject) {

    }

    @Override
    protected Class<ImageResult> getObjectClass() {
        return ImageResult.class;
    }


    @Transactional
    public void saveImage(String name, String algorithm, Integer ratio, String type, byte[] data, Image original) throws IOException {

        ImageResult image = new ImageResult(name,algorithm ,ratio,type, data, original);
        image.ensureExternalId();
        repository.save(image);
        /*user.setImage(imageService.save(image));*/
        /*this.update(user.getId(), user);*/
        System.out.println("finish updating user pict..");
        LOGGER.info("The image has been created from {}", name);

    }

    @Transactional
    public Optional<ImageResult> findByExternalIdAndFetchImageEagerly(String externalId) throws IOException {
        Specification<ImageResult> spec = (root, query, cb) -> cb.equal(root.get("externalId"), externalId);
        // Obtiene la primera página de imágenes del repositorio
        var imagePage = repository.findByExternalId(externalId);
        return  repository.findByExternalIdAndFetchImageEagerly(externalId);   //new ArrayList<>();
    }


    @Transactional(readOnly = true)
    public Page<ImageResult> findPageImages(Long externalId, int page, int pageSize, Sort.Direction order, String... propertiesToOrder) {
        Sort sortBy = Sort.by(order, propertiesToOrder);
        Specification<ImageResult> spec = (root, query, cb) -> cb.equal(root.get("originalImageId"), externalId);
        // Obtiene la primera página de imágenes del repositorio
        Page<ImageResult> imagePage = repository.findAll(spec, PageRequest.of(page, pageSize, sortBy));

        return imagePage;
    }

}

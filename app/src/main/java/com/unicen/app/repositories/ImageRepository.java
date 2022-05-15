package com.unicen.app.repositories;


import com.unicen.app.model.Image;

import java.util.Optional;

public interface ImageRepository extends PublicObjectRepository<Image, Long> {
    Optional<Image> getImageByUrl(String url);
}
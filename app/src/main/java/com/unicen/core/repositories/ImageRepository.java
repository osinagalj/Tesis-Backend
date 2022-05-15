package com.unicen.core.repositories;


import com.unicen.core.model.Image;

import java.util.Optional;

public interface ImageRepository extends PublicObjectRepository<Image, Long> {
    Optional<Image> getImageByUrl(String url);
}
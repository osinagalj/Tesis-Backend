package com.unicen.app.repository;

import com.unicen.app.model.Image;
import com.unicen.core.repositories.PublicObjectRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends PublicObjectRepository<Image, Long> {
}

package com.unicen.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "core_image")
@NoArgsConstructor
public class Image extends PublicModel {
    private String url;
    private String type;
    private int width;
    private int height;
    private String description;

    public Image(String url, String type, int width, int height, String description) {
        this.url = url;
        this.type = type;
        this.width = width;
        this.height = height;
        this.description = description;
        ensureExternalId();
    }

    // Copy constructor
    public Image(Image image) {
        this(image.getUrl(), image.getType(), image.getWidth(), image.getHeight(), image.getDescription());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Image image = (Image) o;
        return externalId.equals(image.externalId) && url.equals(image.url) && type.equals(image.type) && width == image.width && height == image.height
                && description.equals(image.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, url, type, width, height, description);
    }
}
package com.unicen.core.model;

/**
 * General definition for a DTO
 *
 * @param <Model> the class that is projected / abstracted by the DTO
 */
public interface DTO<Model> {

    /**
     * Method invoked when a DTO is created from {@code model}
     * @param model the model object from which the DTO has been automatically generated
     *              by using a semi-automatic mapping approach
     */
    default void postConstructFrom(Model model) {
    }

    /**
     * Method invoked when a Model is created from the current DTO
     *
     * @param model the model that was created from this DTO instance by using semi-automatic
     *              mapping approaches so it can be tuned properly
     */
    default void afterGenerating(Model model) {
    }

}

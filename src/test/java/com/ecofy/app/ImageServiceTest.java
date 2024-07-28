package com.ecofy.app;

import com.ecofy.app.model.Image;
import com.ecofy.app.model.ImageType;
import com.ecofy.app.service.ImageService;
import com.ecofy.core.exceptions.CoreApiException;
import com.ecofy.core.model.User;
import com.ecofy.core.repositories.AuthenticationTokenRepository;
import com.ecofy.core.services.EntitiesDrawer;
import com.ecofy.core.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.ecofy.Utils.closeInputStream;
import static com.ecofy.Utils.compararInputStreams;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;


    @Test
    public void shouldRetrieveSameImagesFromResources() throws IOException {
        InputStream inputStream1 = getClass().getResourceAsStream("/static/originals/01.jpg");
        InputStream inputStream2 = getClass().getResourceAsStream("/static/originals/011.jpg");

        try {
            // Comparar los datos leídos de los InputStreams
            Boolean sameInput = compararInputStreams(inputStream1, inputStream2);
            assertTrue(sameInput);
        } finally {
            closeInputStream(inputStream1);
            closeInputStream(inputStream2);
        }
    }

    @Test
    public void shouldRetrieveDifferentImagesFromResources() throws IOException {
        InputStream inputStream1 = getClass().getResourceAsStream("/static/originals/01.jpg");
        InputStream inputStream2 = getClass().getResourceAsStream("/static/originals/02.jpg");

        try {
            // Comparar los datos leídos de los InputStreams
            Boolean sameInput = compararInputStreams(inputStream1, inputStream2);
            assertFalse(sameInput);
        } finally {
            closeInputStream(inputStream1);
            closeInputStream(inputStream2);
        }
    }




    @Test
    public void updatePictureOfUser() {
        User user = EntitiesDrawer.getAdminUser();

        Image image = new Image("Image from test 2",1,11,"U","T","D",null, null, null, null);
        image.ensureExternalId();
        user.setImage(imageService.save(image));
        userService.update(user.getId(), user);
        System.out.println("finish..");
    }

    @Test
    public void getImageRandom() {
        System.out.println("starting..");
        var a = imageService.getById(1L);
        System.out.println("finish..");
    }

    /*
    @Test
    public void getUserWithPicture() throws IOException {
        System.out.println("starting..");
        var a = userService.findByExternalIdAndFetchImageEagerly("f82b162417214c838c007fe33b9a89ef");
        System.out.println("finish..");
    }

    @Test
    public void getUserWithoutPicture() throws IOException {
        System.out.println("starting..");
        var a = userService.getByExternalId("f82b162417214c838c007fe33b9a89ef");
        System.out.println("finish..");
    }
    */

}
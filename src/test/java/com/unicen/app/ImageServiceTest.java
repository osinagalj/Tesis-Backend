package com.unicen.app;

import com.unicen.app.model.Image;
import com.unicen.app.service.ImageService;
import com.unicen.core.model.User;
import com.unicen.core.repositories.AuthenticationTokenRepository;
import com.unicen.core.services.EntitiesDrawer;
import com.unicen.core.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationTokenRepository authenticationTokenRepository;


    @Test
    public void updatePictureOfUser() {
        User user = EntitiesDrawer.getAdminUser();

        Image image = new Image("Image from test 2",1,11,"U","T","D",null, null);
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

    @Test
    public void getUserWithPicture() throws IOException {
        System.out.println("starting..");
        var a = userService.findByExternalIdAndFetchImageEagerly("831c8949bac244149f46c10161794bce");
        System.out.println("finish..");
    }

    @Test
    public void getUserWithoutPicture() throws IOException {
        System.out.println("starting..");
        var a = userService.getByExternalId("831c8949bac244149f46c10161794bce");
        System.out.println("finish..");
    }
}
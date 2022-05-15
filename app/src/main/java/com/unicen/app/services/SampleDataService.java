package com.unicen.app.services;


import com.unicen.app.model.User;
import org.springframework.stereotype.Service;

/*
  use EntitiesDrawer instead
 */
@Service
@Deprecated(forRemoval = true)
public class SampleDataService {

    /*
      inline method to use EntitiesDrawer EntitiesDrawer.adminUser()
     */
    @Deprecated(forRemoval = true)
    public User ensureInitialUser() {
        return EntitiesDrawer.adminUser();
    }

}
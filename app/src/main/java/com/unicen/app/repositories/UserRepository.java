package com.unicen.app.repositories;


import com.unicen.app.model.User;

import java.util.Optional;

public interface UserRepository extends PublicObjectRepository<User, Long> {

    Optional<User> findByEmail(String email);
}

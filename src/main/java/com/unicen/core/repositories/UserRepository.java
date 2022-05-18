package com.unicen.core.repositories;


import com.unicen.core.model.User;

import java.util.Optional;

public interface UserRepository extends PublicObjectRepository<User, Long> {

    Optional<User> findByEmail(String email);
}

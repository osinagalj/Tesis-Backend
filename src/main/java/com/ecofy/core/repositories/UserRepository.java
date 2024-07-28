package com.ecofy.core.repositories;


import com.ecofy.core.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends PublicObjectRepository<User, Long> {

    Optional<User> findByEmail(String email);

/*
    @Query(value = "Select u FROM User u JOIN FETCH u.image WHERE u.external_id = (:userExternalId)")
    @Param("userExternalId") String userExternalId)

    For PLSQL you have to use the name of the objet 'User', no the name of the table 'core_user'
*/

    @Query("Select u FROM User u JOIN FETCH u.image where u.externalId  = :userExternalId")
    Optional<User> findByExternalIdAndFetchImageEagerly(@Param("userExternalId") String userExternalId);




}

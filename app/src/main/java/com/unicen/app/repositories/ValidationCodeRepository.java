package com.unicen.app.repositories;

import com.unicen.app.model.ValidationCode;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ValidationCodeRepository extends PagingAndSortingRepository<ValidationCode, Long> {

    Optional<ValidationCode> findByCode(String code);

    Optional<ValidationCode> findByCodeAndUserId(String code, long userId);

    Optional<ValidationCode> findByCodeAndValidationInformation(String code, String id);

    Optional<ValidationCode> findByValidationInformation(String email);
}

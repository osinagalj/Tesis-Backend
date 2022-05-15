package com.unicen.app.repositories;

import com.unicen.app.model.ApiKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface ApiKeyRepository extends PublicObjectRepository<ApiKey, Long> {
    @Transactional(readOnly = true)
    @Query("SELECT k FROM ApiKey k WHERE k.disabledAt IS NULL AND k.issuer.disabledAt IS NULL AND k.expirationDate > current_timestamp AND k.keyValue = :keyValue")
    Optional<ApiKey> getByKeyValue(@Param("keyValue") String keyValue);
}

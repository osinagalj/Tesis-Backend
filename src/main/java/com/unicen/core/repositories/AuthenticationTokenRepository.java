package com.unicen.core.repositories;

import com.unicen.core.model.AuthenticationToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Transactional
public interface AuthenticationTokenRepository extends PagingAndSortingRepository<AuthenticationToken, Long> {

    @Query("FROM AuthenticationToken WHERE expiresAt is null or expiresAt > now() and token = :token")
    Optional<AuthenticationToken> getActiveToken(@Param("token") String token);

    @Transactional(readOnly = true)
    Optional<AuthenticationToken> getByToken(@Param("token") String token);

    @Query(value = "SELECT * FROM core_auth_token WHERE user_id = :id ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<AuthenticationToken> getByUserId(@Param("id") long id);

    @Modifying
    @Query("UPDATE AuthenticationToken at SET expiresAt = now() where at.user.id = :userId")
    void invalidateOldTokensForUserId(@Param("userId") long userId);

    @Modifying
    @Query("DELETE FROM AuthenticationToken at WHERE at.expiresAt < :date")
    int deleteTokensExpiredBefore(@Param("date") Date date);

    @Modifying
    @Query("UPDATE AuthenticationToken at SET expiresAt = now() where at.token = :token")
    void invalidateToken(@Param("token") String token);

}

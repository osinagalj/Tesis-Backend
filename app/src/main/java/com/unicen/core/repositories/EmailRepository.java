package com.unicen.core.repositories;

import com.unicen.core.model.Email;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmailRepository extends PagingAndSortingRepository<Email, Long> {

    /**
     * Get all the emails sent by a specific User (by its email)
     *
     * @param senderEmail User's Email used to send mails
     * @return List of all the Email's sent by {@param senderEmail}
     */
    @Query(value = "SELECT to, subject, body FROM Email WHERE sender = :senderEmail")
    Optional<Email> getBySenderEmail(@Param("senderEmail") String senderEmail);
}

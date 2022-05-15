package com.unicen.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;


import java.util.concurrent.TimeUnit;

@Service
public class SecretsService {
    private static final Logger logger = LoggerFactory.getLogger(SecretsService.class);
    private static final String LOG_PREFIX = "[AWS-SECRETS]";
    private static final long CACHE_DURATION_SECONDS = TimeUnit.DAYS.toSeconds(1);


    public SecretsService() {

    }

    /**
     * Creates a secret in AWS SecretManager
     *
     * @param secretName the name of the secret
     * @param secretValue the values of the secret
     * @param description the description of the secret
     * @return the secret's arn
     */
    public String createSecret(String secretName, String secretValue, String description) {
        return null;
    }


}

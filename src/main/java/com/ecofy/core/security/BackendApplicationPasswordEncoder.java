package com.ecofy.core.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BackendApplicationPasswordEncoder implements UserPasswordEncoder {

    private PasswordEncoder passwordEncoder;

    public BackendApplicationPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String plainPassword) {
        return this.passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String hashedPassword) {
        return this.passwordEncoder.matches(plainPassword, hashedPassword);
    }
}

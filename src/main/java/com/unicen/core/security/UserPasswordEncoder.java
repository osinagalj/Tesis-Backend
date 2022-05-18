package com.unicen.core.security;

public interface UserPasswordEncoder {

    String encode(String plainPassword);

    boolean matches(String plainPassword, String hashedPassword);

}

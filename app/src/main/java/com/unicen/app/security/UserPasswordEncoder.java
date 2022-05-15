package com.unicen.app.security;

public interface UserPasswordEncoder {

    String encode(String plainPassword);

    boolean matches(String plainPassword, String hashedPassword);

}

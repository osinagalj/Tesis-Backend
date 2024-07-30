package com.unicen.core.security.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomStringGenerator {

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    public static final String UPPERCASE_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String LOWERCASE_SYMBOLS = UPPERCASE_SYMBOLS.toLowerCase(Locale.ROOT);

    public static final String DIGITS_SYMBOLS = "0123456789";

    public static final String ALPHANUMERIC_SYMBOLS = UPPERCASE_SYMBOLS + LOWERCASE_SYMBOLS + DIGITS_SYMBOLS;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomStringGenerator(int length, Random random, String symbols) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }

        if (symbols.length() < 2)
            throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * Create an alphanumeric string generator.
     */
    public RandomStringGenerator(int length, Random random) {
        this(length, random, ALPHANUMERIC_SYMBOLS);
    }

    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomStringGenerator(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomStringGenerator() {
        this(21);
    }

}
package com.jpmorgan.processor.util;

import java.util.Objects;

/**
 * Utility class for helping purposes
 */
public final class Assert {

    private Assert() {
        throw new IllegalArgumentException();
    }

    public static <T> T notNull(T object, String message) {
        if (Objects.isNull(object)) {
            throw new IllegalArgumentException(message);
        }
        return object;
    }

    public static void hasText(String target, String message) {
        if (Objects.isNull(target) || target.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean value, String message) {
        if (!value) {
            throw new IllegalArgumentException(message);
        }
    }

}

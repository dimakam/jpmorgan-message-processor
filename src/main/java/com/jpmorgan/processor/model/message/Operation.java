package com.jpmorgan.processor.model.message;

import java.util.Arrays;

import com.jpmorgan.processor.util.Assert;

/**
 * Represents math operations that might be applied to sale
 */
public enum Operation {

    ADD, SUBTRACT, MULTIPLY;

    public static Operation valueFor(String operation) {
        Assert.hasText(operation, "operation is required");

        return Arrays.stream(values())
            .filter(i -> i.name().equalsIgnoreCase(operation))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Does not support operation: " + operation));
    }

}

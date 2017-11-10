package com.jpmorgan.processor.deser.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpmorgan.processor.model.message.AdjacentMessage;
import com.jpmorgan.processor.model.message.Operation;
import com.jpmorgan.processor.model.message.SaleMessage;

/**
 * Converter responsible for converting message into {@link AdjacentMessage}
 */
public class AdjacentConverter implements Converter<AdjacentMessage> {

    private static final Pattern FORMAT = Pattern.compile(
        "^(?<OPERATION>[a-zA-Z]+) (?<PRICE>\\d+)p (?<NAME>\\p{Graph}+)[es|s]+$", Pattern.CASE_INSENSITIVE);

    @Override
    public AdjacentMessage convert(String message) {
        Matcher matcher = FORMAT.matcher(message);
        if (matcher.find()) {
            return new AdjacentMessage(
                Operation.valueFor(matcher.group("OPERATION")),
                new SaleMessage(matcher.group("NAME"), Integer.parseInt(matcher.group("PRICE")), 0x0)
            );
        }

        throw new IllegalArgumentException("message cannot be converted");
    }

    @Override
    public boolean isSupported(String message) {
        return FORMAT.asPredicate().test(message);
    }

}

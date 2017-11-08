package com.jpmorgan.processor.deser.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jpmorgan.processor.model.message.SaleMessage;

/**
 * Converter responsible for converting message into {@link SaleMessage}
 */
public class SaleConverter implements Converter<SaleMessage> {

    private static final Pattern SINGLE_SALE_FORMAT = Pattern.compile(
        "^(?<NAME>\\p{Graph}+) at (?<PRICE>\\d+)p$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BULK_SALE_FORMAT = Pattern.compile(
        "^(?<QUANTITY>\\d+) sale[s] of (?<NAME>\\p{Graph}+)[es|s]+ at (?<PRICE>\\d+)p [\\w]*$", Pattern.CASE_INSENSITIVE);

    @Override
    public SaleMessage convert(String message) {
        Matcher matcher = SINGLE_SALE_FORMAT.matcher(message);
        if (matcher.find()) {
            return new SaleMessage(matcher.group("NAME"), Integer.parseInt(matcher.group("PRICE")));
        } else {
            matcher = BULK_SALE_FORMAT.matcher(message);
            if (matcher.find()) {
                return new SaleMessage(matcher.group("NAME"),
                    Integer.parseInt(matcher.group("PRICE")),
                    Integer.parseInt(matcher.group("QUANTITY"))
                );
            }
        }

        throw new IllegalArgumentException("message cannot be converted");
    }

    @Override
    public boolean isSupported(String message) {
        return SINGLE_SALE_FORMAT.asPredicate().test(message)
            || BULK_SALE_FORMAT.asPredicate().test(message);
    }
}

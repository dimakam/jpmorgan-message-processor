package com.jpmorgan.processor.deser.converter;

import com.jpmorgan.processor.model.message.Message;

/**
 * Represents basic interface for converters
 *
 * @param <T> the parameter of converter
 */
public interface Converter<T extends Message> {

    /**
     * Converts message to type T
     *
     * @param message the message to be converted
     * @return converted message
     */
    T convert(String message);

    /**
     * Check weather message can be converted or not
     *
     * @param message the message
     * @return true if message can be converted, otherwise false
     */
    boolean isSupported(String message);

}

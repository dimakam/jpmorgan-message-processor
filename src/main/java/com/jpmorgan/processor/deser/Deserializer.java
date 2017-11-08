package com.jpmorgan.processor.deser;

import java.util.ArrayList;
import java.util.List;

import com.jpmorgan.processor.model.message.Message;
import com.jpmorgan.processor.deser.converter.Converter;
import com.jpmorgan.processor.util.Assert;

public class Deserializer {

    private List<Converter<? extends Message>> converters = new ArrayList<>();

    public Deserializer registerConverter(Converter<? extends Message> converter) {
        converters.add(Assert.notNull(converter, "converter is required"));

        return this;
    }

    public Message deserialize(String message) {
        Assert.hasText(message, "message is required");

        return converters.stream()
            .filter(i -> i.isSupported(message))
            .findFirst()
            .map(i -> i.convert(message))
            .orElseThrow(() -> new IllegalArgumentException(message + " cannot be converted"));
    }

}

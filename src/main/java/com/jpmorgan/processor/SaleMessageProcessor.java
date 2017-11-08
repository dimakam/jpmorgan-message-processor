package com.jpmorgan.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.jpmorgan.processor.deser.Deserializer;
import com.jpmorgan.processor.deser.converter.AdjacentConverter;
import com.jpmorgan.processor.deser.converter.SaleConverter;
import com.jpmorgan.processor.entity.Adjacent;
import com.jpmorgan.processor.entity.Sale;
import com.jpmorgan.processor.model.Statistics;
import com.jpmorgan.processor.model.message.AdjacentMessage;
import com.jpmorgan.processor.model.message.Message;
import com.jpmorgan.processor.model.message.SaleMessage;
import com.jpmorgan.processor.util.Assert;

/**
 * Represents basic sale message processing
 */
public final class SaleMessageProcessor implements MessageProcessor {

    private final static Deserializer DESERIALIZER = new Deserializer()
        .registerConverter(new SaleConverter    ())
        .registerConverter(new AdjacentConverter());

    private final Map<String, List<SaleMessage>> saleMessages = new HashMap<>();
    private final Map<String, Sale> salesHolder = new HashMap<>();
    private final Map<String, List<Adjacent>> adjacentsHolder = new HashMap<>();
    private final Map<Class<? extends Message>, Function<Message, Statistics>> actions = new HashMap<>();

    public SaleMessageProcessor() {
        actions.put(SaleMessage    .class, c -> perform((SaleMessage    ) c));
        actions.put(AdjacentMessage.class, c -> perform((AdjacentMessage) c));
    }

    @Override
    public Statistics process(final String message) {
        Assert.hasText(message, "message is required");

        Message beanMessage = DESERIALIZER.deserialize(message);
        return Optional.ofNullable(actions.get(beanMessage.getClass()))
            .map(i -> i.apply(beanMessage))
            .orElseThrow(() -> new IllegalArgumentException("Does not support beanMessage with type: " + beanMessage.getClass().getSimpleName()));
    }

    private Statistics perform(SaleMessage message) {
        salesHolder.compute(
            message.getProduct(),
            (k, v) ->
                Optional.ofNullable(v)
                    .map(i -> i.append(Sale.newSale(message)))
                    .orElse(Sale.newSale(message)));
        saleMessages.merge(
            message.getProduct(), Collections.singletonList(message),
            (a, b) -> {
                List<SaleMessage> join = new ArrayList<>(a);
                join.addAll(new ArrayList<>(b));
                return join;
            }
        );
        List<Adjacent> adjacents;
        if (adjacentsHolder.isEmpty()) {
            adjacents = Collections.emptyList();
        } else {
            adjacents = getAdjacents();
        }
        return Statistics.of(salesHolder.values(), adjacents);
    }

    private Statistics perform(AdjacentMessage message) {
        String product = message.getSaleMessage().getProduct();
        List<SaleMessage> adjusted = saleMessages.computeIfPresent(product,
            (a, b) ->
                b.stream()
                    .map(i -> i.apply(message.getOperation(), message.getSaleMessage()))
                    .collect(Collectors.toList())
        );
        if (Objects.isNull(adjusted) || adjusted.isEmpty()) {
            return Statistics.of(salesHolder.values(), getAdjacents());
        }
        Sale newSale = adjusted.stream()
            .map(Sale::newSale)
            .reduce(Sale::append)
            .orElseThrow(() -> new IllegalStateException("Unexpected exception"));
        Sale previousSale = salesHolder.put(product, newSale);
        Adjacent adjacent = Optional.ofNullable(previousSale)
            .map(i -> new Adjacent(i, newSale, message.getOperation(), message.getSaleMessage().getPrice()))
            .orElse(new Adjacent(newSale, message.getOperation(), message.getSaleMessage().getPrice()));

        adjacentsHolder.merge(
            product, Collections.singletonList(adjacent),
            (a, b) -> {
                List<Adjacent> join = new ArrayList<>(a);
                join.addAll(new ArrayList<>(b));
                return join;
            }
        );
        return Statistics.of(salesHolder.values(), getAdjacents());
    }

    private List<Adjacent> getAdjacents() {
        return adjacentsHolder.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

}

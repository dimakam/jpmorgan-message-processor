package com.jpmorgan.processor.entity;

import java.util.Optional;

import com.jpmorgan.processor.model.message.Operation;
import com.jpmorgan.processor.util.Assert;

public class Adjacent {

    private final Optional<Sale> previousSale;
    private final Sale newSale;
    private final Operation operation;
    private final int delta;

    public Adjacent(Sale newSale, Operation operation, int delta) {
        this(null, newSale, operation, delta);
    }

    public Adjacent(Sale previousSale, Sale newSale, Operation operation, int delta) {
        this.newSale = Assert.notNull(newSale, "newSale is required");
        this.operation = Assert.notNull(operation, "operation is required");
        this.previousSale = Optional.ofNullable(previousSale);
        this.delta = delta;
    }

    public Optional<Sale> getPreviousSale() {
        return previousSale;
    }

    public Sale getNewSale() {
        return newSale;
    }

    @Override
    public String toString() {
        return String.join(" ",
            operation.name(), String.valueOf(delta) + "p",  "for product", newSale.getProduct(),
            "with quantity", String.valueOf(newSale.getQuantity()) + ".",
            "Price adjusted from",
            previousSale.map(i -> i.getTotalPrice() + "p").orElse("0p"), "to", newSale.getTotalPrice() + "p");
    }
}

package com.jpmorgan.processor.model.message;

import com.jpmorgan.processor.util.Assert;

/**
 * Represents message that contains information regarding any changes in a sale
 */
public final class AdjacentMessage implements Message {

    private final SaleMessage saleMessage;
    private final Operation operation;

    public AdjacentMessage(final Operation operation, final SaleMessage saleMessage) {
        this.saleMessage = Assert.notNull(saleMessage, "sale is required");
        this.operation = Assert.notNull(operation, "operation is required");
    }

    public SaleMessage getSaleMessage() {
        return saleMessage;
    }

    public Operation getOperation() {
        return operation;
    }
}

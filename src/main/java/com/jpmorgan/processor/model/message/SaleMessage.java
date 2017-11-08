package com.jpmorgan.processor.model.message;

import com.jpmorgan.processor.util.Assert;

/**
 * Represents message that contains information about sale
 */
public class SaleMessage implements Message {

    private final String product;
    private final int price;
    private final int quantity;

    public SaleMessage(final String product, final int price) {
        this(product, price, 1);
    }

    public SaleMessage(final String product, final int price, final int quantity) {
        Assert.hasText(product, "product is required");
        Assert.isTrue(quantity >= 0, "quantity cannot be less than 0");

        this.product = product.trim().toLowerCase();
        this.price = price;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getProduct() {
        return product;
    }

    public int getPrice() {
        return price;
    }

    public SaleMessage apply(final Operation operation, final SaleMessage saleMessage) {
        Assert.notNull(operation, "operation is required");
        Assert.notNull(saleMessage, "saleMessage is required");

        if (!product.equalsIgnoreCase(saleMessage.getProduct())) {
            throw new IllegalArgumentException("Product must be the same");
        }

        int newPrice;
        switch (operation) {
            case SUBTRACT:
                newPrice = price - saleMessage.getPrice();
                break;
            case MULTIPLY:
                newPrice = price * saleMessage.getPrice();
                break;
            case ADD:
                newPrice = price + saleMessage.getPrice();
                break;
            default:
                throw new UnsupportedOperationException("Does not support " + operation);
        }
        return new SaleMessage(product, newPrice, quantity);
    }
}

package com.jpmorgan.processor.entity;

import java.util.Objects;

import com.jpmorgan.processor.model.message.SaleMessage;
import com.jpmorgan.processor.util.Assert;

public final class Sale {

    private final String product;
    private final int quantity;
    private final int totalPrice;

    public Sale(String product, int totalPrice, int quantity) {
        Assert.hasText(product, "product is required");
        Assert.isTrue(quantity >= 0, "quantity must be positive");

        this.product = product;
        this.totalPrice = totalPrice;
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Sale append(Sale sale) {
        validate(sale);

        return new Sale(product, totalPrice + sale.getTotalPrice(), quantity + sale.getQuantity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, totalPrice);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (Objects.isNull(obj) || getClass() != obj.getClass()) {
            return false;
        }
        Sale sale = (Sale) obj;
        return quantity == sale.quantity &&
            totalPrice == sale.totalPrice &&
            Objects.equals(product, sale.product);
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("product: "   ).append(product   ).append(", " )
            .append("totalPrice: ").append(totalPrice).append("p, ")
            .append("quantity: "  ).append(quantity  ).append(""   )
            .toString();
    }

    private void validate(Sale sale) {
        if (!product.equalsIgnoreCase(sale.getProduct())) {
            throw new IllegalArgumentException("Cannot add because of different product's type");
        }
    }

    public static Sale newSale(SaleMessage message) {
        Assert.notNull(message, "message is required");

        return new Sale(message.getProduct(), message.getPrice() * message.getQuantity(), message.getQuantity());
    }
}

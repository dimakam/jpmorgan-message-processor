package com.jpmorgan.processor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.jpmorgan.processor.entity.Adjacent;
import com.jpmorgan.processor.entity.Sale;

public final class Statistics {

    private final List<Sale> sales = new ArrayList<>();
    private final List<Adjacent> adjacents = new ArrayList<>();

    private Statistics() {
    }

    public List<Sale> getSales() {
        return Collections.unmodifiableList(sales);
    }

    public List<Adjacent> getAdjacents() {
        return Collections.unmodifiableList(adjacents);
    }

    private Statistics addSales(Collection<Sale> sales) {
        Optional.ofNullable(sales)
            .ifPresent(this.sales::addAll);

        return this;
    }

    private Statistics addAdjacents(Collection<Adjacent> adjacents) {
        Optional.ofNullable(adjacents)
            .ifPresent(this.adjacents::addAll);

        return this;
    }

    public static Statistics of(Collection<Sale> sales, Collection<Adjacent> adjacents) {
        return new Statistics()
            .addSales(sales)
            .addAdjacents(adjacents);
    }

}

package com.jpmorgan.processor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.jpmorgan.processor.entity.Adjacent;
import com.jpmorgan.processor.entity.Sale;
import com.jpmorgan.processor.model.Statistics;
import org.junit.Assert;
import org.junit.Test;

public class SaleMessageProcessorTest {

    private final MessageProcessor processor = new SaleMessageProcessor();

    @Test
    public void testSingleSale() {
        String message = "strawberry at 5p";
        Statistics statistics = processor.process(message);

        Assert.assertTrue(statistics.getAdjacents().isEmpty());
        Assert.assertEquals(0x1, statistics.getSales().size());

        Sale sale = statistics.getSales().get(0x0);
        Assert.assertEquals(sale.getProduct(), "strawberry");
        Assert.assertEquals(sale.getQuantity(), 0x1);
        Assert.assertEquals(sale.getTotalPrice(), 0x5);
    }

    @Test
    public void testBulkSale() {
        List<String> messages = Arrays.asList("car at 10000p", "2 sales of cars at 15000p each");
        List<Sale> salesPattern = Arrays.asList(
            new Sale("car", 10_000, 1),
            new Sale("car", 40_000, 3)
        );

        List<Statistics> statistics = messages.stream()
            .map(processor::process)
            .collect(Collectors.toList());

        List<Sale> sales = statistics.stream()
            .map(Statistics::getSales)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        Assert.assertEquals(sales, salesPattern);
    }

    @Test
    public void testAdjacent() {
        List<String> messages = Arrays.asList("car at 10000p", "2 sales of cars at 15000p each", "subtract 10000p cars");
        final Sale one = new Sale("car", 10_000, 1);
        final Sale two = new Sale("car", 40_000, 3);
        final Sale three = new Sale("car", 10_000, 3);
        List<Sale> salePatterns = Arrays.asList(one, two, three);
        List<Statistics> statistics = messages.stream()
            .map(processor::process)
            .collect(Collectors.toList());
        List<Sale> sales = statistics.stream()
            .map(Statistics::getSales)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        Assert.assertEquals(sales, salePatterns);
        Assert.assertEquals(0x3, sales.size());
        Assert.assertTrue(statistics.get(0x0).getAdjacents().isEmpty());
        Assert.assertTrue(statistics.get(0x1).getAdjacents().isEmpty());

        List<Adjacent> adjacents = statistics.get(0x2).getAdjacents();
        Assert.assertTrue(adjacents.get(0x0).getPreviousSale().isPresent());
        Assert.assertEquals(two, adjacents.get(0x0).getPreviousSale().get());
        Assert.assertEquals(three, adjacents.get(0x0).getNewSale());
    }

    @Test
    public void testAdjacentWithoutSells() {
        String message = "subtract 10000p cars";
        Statistics statistics = processor.process(message);

        Assert.assertTrue(statistics.getAdjacents().isEmpty());
        Assert.assertTrue(statistics.getSales().isEmpty());
    }
}
package com.jpmorgan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.jpmorgan.processor.MessageProcessor;
import com.jpmorgan.processor.SaleMessageProcessor;
import com.jpmorgan.processor.entity.Adjacent;
import com.jpmorgan.processor.entity.Sale;
import com.jpmorgan.processor.model.Statistics;
import com.jpmorgan.processor.util.Assert;

public class AppRunner {

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
    }

    private static final Logger LOGGER = Logger.getLogger(AppRunner.class.getName());
    private static final int DEFAULT_THROUGHPUT = 50;
    private static final int DEFAULT_BATCH = 10;

    private final String source;
    private final int throughput;
    private final int batch;

    public AppRunner(int throughput, int batch, String source) {
        Assert.hasText(source, "source is required");
        Assert.isTrue(throughput > 0, "throughput cannot be less than 0");
        Assert.isTrue(batch > 0, "batch cannot be less than 0");
        Assert.isTrue(throughput >= batch, "throughput cannot be less than batch");

        this.batch = batch;
        this.throughput = throughput;
        this.source = source.trim();
    }

    public void run() {
        int counter = 0;
        MessageProcessor processor = new SaleMessageProcessor();
        try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
            String message;
            while (null != (message = reader.readLine())) {
                counter++;
                Statistics statistics = processor.process(message);
                if (0x0 == counter % batch) {
                    LOGGER.info("\n" + counter + " messages have been processed");
                    printSales(statistics.getSales());
                }
                if (counter == throughput) {
                    LOGGER.info("Application reached " + throughput + " messages and stopping further processing...");
                    printAdjacents(statistics.getAdjacents());

                    System.exit(0x0);
                }
            }

        } catch (final IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    private void printAdjacents(List<Adjacent> adjacents) {
        Optional.of(adjacents)
            .filter(i -> !i.isEmpty())
            .ifPresent(i -> LOGGER.info("There are some adjustment records left:\n" +
                i.stream()
                    .map(Adjacent::toString)
                    .collect(Collectors.joining("\n")) + "\n"
                )
            );
    }

    private void printSales(List<Sale> sales) {
        LOGGER.info(sales.stream()
            .map(Sale::toString)
            .collect(Collectors.joining("\n")) + "\n"
        );
    }

    public static void main(String[] args) {
        if (Objects.isNull(args) || 0x1 != args.length) {
            LOGGER.severe("\nIt's required to specify path to the file with messages");
            System.exit(0x1);
        }

        new AppRunner(DEFAULT_THROUGHPUT, DEFAULT_BATCH, args[0]).run();
    }

}

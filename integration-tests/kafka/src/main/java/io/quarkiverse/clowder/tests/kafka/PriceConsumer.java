package io.quarkiverse.clowder.tests.kafka;

import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class PriceConsumer {

    private final List<Double> prices = new ArrayList<>();

    @Incoming("prices")
    public void consume(double price) {
        prices.add(price);
    }

    public List<Double> getPrices() {
        return prices;
    }
}

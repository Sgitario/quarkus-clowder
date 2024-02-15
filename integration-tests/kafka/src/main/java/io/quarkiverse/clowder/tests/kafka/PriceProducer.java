package io.quarkiverse.clowder.tests.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class PriceProducer {
    @Inject
    @Channel("price-out")
    Emitter<Double> priceEmitter;

    public void send(Double price) {
        priceEmitter.send(price);
    }
}

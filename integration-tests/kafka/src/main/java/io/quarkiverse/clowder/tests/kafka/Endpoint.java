package io.quarkiverse.clowder.tests.kafka;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("")
public class Endpoint {

    @Inject
    PriceConsumer priceConsumer;

    @Inject
    PriceProducer priceProducer;

    @ConfigProperty(name = "mp.messaging.incoming.prices.topic")
    String pricesTopic;

    @ConfigProperty(name = "mp.messaging.outgoing.price-out.topic")
    String priceOutTopic;

    @GET
    @Path("/prices/topic")
    public String getPricesTopic() {
        return pricesTopic;
    }

    @GET
    @Path("/priceOut/topic")
    public String getPriceOutTopic() {
        return priceOutTopic;
    }

    @PUT
    @Path("/prices/{price}")
    public void addPrice(Double price) {
        priceProducer.send(price);
    }

    @GET
    @Path("/prices/first")
    public double getLastPrice() {
        var prices = priceConsumer.getPrices();
        if (prices.isEmpty()) {
            return Double.MIN_VALUE;
        }

        return prices.get(prices.size() - 1);
    }
}

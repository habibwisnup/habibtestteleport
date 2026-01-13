package com.habib.testhabib.validator;
import com.habib.testhabib.model.OptimizeRequest;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public void validate(OptimizeRequest req) {
        if (req == null || req.truck() == null)
            throw new IllegalArgumentException("Truck is required");

        if (req.orders() == null)
            throw new IllegalArgumentException("Orders list is required");

        if (req.orders().size() > 22)
            throw new IllegalArgumentException("Max 22 orders supported");

        req.orders().forEach(o -> {
            if (o.pickupDate().isAfter(o.deliveryDate()))
                throw new IllegalArgumentException(
                        "Pickup date after delivery date for order " + o.id()
                );
            if (o.payoutCents() < 0)
                throw new IllegalArgumentException("Negative payout not allowed");
        });
    }
}

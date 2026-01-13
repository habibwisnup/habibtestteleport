package com.habib.testhabib.service;
import com.habib.testhabib.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class LoadOptimizerService {

    public OptimizeResponse optimize(OptimizeRequest req) {
        Truck truck = req.truck();
        List<Order> orders = preFilter(req.orders(), truck);

        int n = orders.size();
        if (n == 0) {
            return emptyResponse(truck);
        }

        int maxMask = 1 << n;

        int[] weight = new int[maxMask];
        int[] volume = new int[maxMask];
        long[] payout = new long[maxMask];
        boolean[] hazmat = new boolean[maxMask];
        LocalDate[] latestPickup = new LocalDate[maxMask];
        LocalDate[] earliestDelivery = new LocalDate[maxMask];

        long bestPayout = 0;
        int bestMask = 0;

        for (int mask = 1; mask < maxMask; mask++) {
            int prev = mask & (mask - 1);
            int bit = mask ^ prev;
            int idx = Integer.numberOfTrailingZeros(bit);

            Order o = orders.get(idx);

            weight[mask] = weight[prev] + o.weightLbs();
            if (weight[mask] > truck.maxWeightLbs()) continue;

            volume[mask] = volume[prev] + o.volumeCuft();
            if (volume[mask] > truck.maxVolumeCuft()) continue;

            // hazmat compatibility
            if (prev == 0) {
                hazmat[mask] = o.isHazmat();
            } else {
                if (hazmat[prev] != o.isHazmat()) continue;
                hazmat[mask] = hazmat[prev];
            }

            if (prev == 0) {
                latestPickup[mask] = o.pickupDate();
                earliestDelivery[mask] = o.deliveryDate();
            } else {
                latestPickup[mask] = latestPickup[prev].isAfter(o.pickupDate())
                        ? latestPickup[prev] : o.pickupDate();
                earliestDelivery[mask] = earliestDelivery[prev].isBefore(o.deliveryDate())
                        ? earliestDelivery[prev] : o.deliveryDate();
                if (latestPickup[mask].isAfter(earliestDelivery[mask])) continue;
            }

            payout[mask] = payout[prev] + o.payoutCents();

            if (payout[mask] > bestPayout) {
                bestPayout = payout[mask];
                bestMask = mask;
            }
        }

        return buildResponse(truck, orders, bestMask, payout, weight, volume);
    }

    private List<Order> preFilter(List<Order> orders, Truck truck) {
        if (orders.isEmpty()) return List.of();

        String origin = orders.getFirst().origin();
        String destination = orders.getFirst().destination();

        return orders.stream()
                .filter(o -> o.origin().equals(origin))
                .filter(o -> o.destination().equals(destination))
                .filter(o -> o.weightLbs() <= truck.maxWeightLbs())
                .filter(o -> o.volumeCuft() <= truck.maxVolumeCuft())
                .toList();
    }

    private OptimizeResponse buildResponse(
            Truck truck,
            List<Order> orders,
            int mask,
            long[] payout,
            int[] weight,
            int[] volume
    ) {
        List<String> selected = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            if ((mask & (1 << i)) != 0) {
                selected.add(orders.get(i).id());
            }
        }

        return new OptimizeResponse(
                truck.id(),
                selected,
                payout[mask],
                weight[mask],
                volume[mask],
                round(weight[mask] * 100.0 / truck.maxWeightLbs()),
                round(volume[mask] * 100.0 / truck.maxVolumeCuft())
        );
    }

    private OptimizeResponse emptyResponse(Truck truck) {
        return new OptimizeResponse(
                truck.id(),
                List.of(),
                0,
                0,
                0,
                0,
                0
        );
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

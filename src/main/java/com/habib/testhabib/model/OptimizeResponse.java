package com.habib.testhabib.model;

import java.util.List;

public record OptimizeResponse(
        String truckId,
        List<String> selectedOrderIds,
        long totalPayoutCents,
        int totalWeightLbs,
        int totalVolumeCuft,
        double utilizationWeightPercent,
        double utilizationVolumePercent
) {}
package com.habib.testhabib.service;

import com.habib.testhabib.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoadOptimizerServiceTest {

    private final LoadOptimizerService service = new LoadOptimizerService();

    @Test
    void shouldReturnOptimalOrders_sampleCase() {
        Truck truck = new Truck(
                "truck-123",
                44_000,
                3_000
        );

        Order o1 = new Order(
                "ord-001",
                250_000L,
                18_000,
                1_200,
                "Los Angeles, CA",
                "Dallas, TX",
                LocalDate.parse("2025-12-05"),
                LocalDate.parse("2025-12-09"),
                false
        );

        Order o2 = new Order(
                "ord-002",
                180_000L,
                12_000,
                900,
                "Los Angeles, CA",
                "Dallas, TX",
                LocalDate.parse("2025-12-04"),
                LocalDate.parse("2025-12-10"),
                false
        );

        Order o3 = new Order(
                "ord-003",
                320_000L,
                30_000,
                1_800,
                "Los Angeles, CA",
                "Dallas, TX",
                LocalDate.parse("2025-12-06"),
                LocalDate.parse("2025-12-08"),
                true
        );

        OptimizeRequest request = new OptimizeRequest(
                truck,
                List.of(o1, o2, o3)
        );

        OptimizeResponse response = service.optimize(request);

        assertThat(response.selectedOrderIds())
                .containsExactlyInAnyOrder("ord-001", "ord-002");

        assertThat(response.totalPayoutCents()).isEqualTo(430_000L);
        assertThat(response.totalWeightLbs()).isEqualTo(30_000);
        assertThat(response.totalVolumeCuft()).isEqualTo(2_100);
    }

    @Test
    void shouldNotMixHazmatAndNonHazmat() {
        Truck truck = new Truck("t1", 44_000, 3_000);

        Order hazmat = new Order(
                "haz",
                300_000L,
                10_000,
                1_000,
                "LA",
                "DAL",
                LocalDate.parse("2025-12-01"),
                LocalDate.parse("2025-12-02"),
                true
        );

        Order normal = new Order(
                "norm",
                400_000L,
                10_000,
                1_000,
                "LA",
                "DAL",
                LocalDate.parse("2025-12-01"),
                LocalDate.parse("2025-12-02"),
                false
        );

        OptimizeRequest request = new OptimizeRequest(
                truck,
                List.of(hazmat, normal)
        );

        OptimizeResponse response = service.optimize(request);

        // Only ONE should be chosen
        assertThat(response.selectedOrderIds()).hasSize(1);
    }

    @Test
    void shouldHandleEmptyOrders() {
        Truck truck = new Truck("t1", 44_000, 3_000);

        OptimizeRequest request = new OptimizeRequest(
                truck,
                List.of()
        );

        OptimizeResponse response = service.optimize(request);

        assertThat(response.selectedOrderIds()).isEmpty();
        assertThat(response.totalPayoutCents()).isZero();
    }

}
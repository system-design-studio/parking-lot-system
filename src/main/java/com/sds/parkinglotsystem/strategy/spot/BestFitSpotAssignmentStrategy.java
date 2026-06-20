package com.sds.parkinglotsystem.strategy.spot;

import com.sds.parkinglotsystem.domain.enums.SpotStatus;
import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.enums.VehicleType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;
import com.sds.parkinglotsystem.repository.ParkingSpotRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Default assignment strategy: walk the vehicle's compatible spot types in
 * best-fit order (tightest fitting type first) and claim the lowest-numbered
 * available spot of the first type that has one. Claiming uses a pessimistic
 * {@code FOR UPDATE SKIP LOCKED} read so concurrent entries never collide on the
 * same spot.
 */
@Component
public class BestFitSpotAssignmentStrategy implements SpotAssignmentStrategy {

    private final ParkingSpotRepository spotRepository;

    public BestFitSpotAssignmentStrategy(ParkingSpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    @Override
    public Optional<ParkingSpot> assignSpot(Long lotId, VehicleType vehicleType) {
        for (SpotType spotType : vehicleType.compatibleSpotTypesByPreference()) {
            List<ParkingSpot> candidates =
                    spotRepository.findClaimableSpots(lotId, spotType, SpotStatus.AVAILABLE, Limit.of(1));
            if (!candidates.isEmpty()) {
                return Optional.of(candidates.get(0));
            }
        }
        return Optional.empty();
    }
}

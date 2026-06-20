package com.sds.parkinglotsystem.strategy.spot;

import com.sds.parkinglotsystem.domain.enums.VehicleType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;

import java.util.Optional;

/**
 * Strategy for selecting (and locking) a spot for an incoming vehicle.
 * Implementations must run inside the caller's transaction so that the returned
 * spot stays locked until the entry transaction commits.
 */
public interface SpotAssignmentStrategy {

    /**
     * Finds and pessimistically locks a free, compatible spot in the given lot.
     *
     * @return the locked spot, or empty if the lot is full for this vehicle type
     */
    Optional<ParkingSpot> assignSpot(Long lotId, VehicleType vehicleType);
}

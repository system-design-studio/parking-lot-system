package com.sds.parkinglotsystem.repository;

import com.sds.parkinglotsystem.domain.model.ParkingFloor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {

    Optional<ParkingFloor> findByParkingLot_IdAndFloorNumber(Long parkingLotId, int floorNumber);
}

package com.sds.parkinglotsystem.repository;

import com.sds.parkinglotsystem.domain.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
}

package com.sds.parkinglotsystem.service;

import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.model.ParkingFloor;
import com.sds.parkinglotsystem.domain.model.ParkingLot;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;
import com.sds.parkinglotsystem.exception.InvalidParkingStateException;
import com.sds.parkinglotsystem.exception.ResourceNotFoundException;
import com.sds.parkinglotsystem.repository.ParkingFloorRepository;
import com.sds.parkinglotsystem.repository.ParkingLotRepository;
import com.sds.parkinglotsystem.repository.ParkingSpotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Administrative operations for building out the physical structure of a lot:
 * creating lots, adding floors, and provisioning spots.
 */
@Service
public class ParkingLotAdminService {

    private final ParkingLotRepository lotRepository;
    private final ParkingFloorRepository floorRepository;
    private final ParkingSpotRepository spotRepository;

    public ParkingLotAdminService(ParkingLotRepository lotRepository,
                                  ParkingFloorRepository floorRepository,
                                  ParkingSpotRepository spotRepository) {
        this.lotRepository = lotRepository;
        this.floorRepository = floorRepository;
        this.spotRepository = spotRepository;
    }

    @Transactional
    public ParkingLot createLot(String name, String address) {
        return lotRepository.save(new ParkingLot(name, address));
    }

    @Transactional(readOnly = true)
    public ParkingLot getLot(Long lotId) {
        return lotRepository.findById(lotId)
                .orElseThrow(() -> ResourceNotFoundException.of("ParkingLot", lotId));
    }

    @Transactional
    public ParkingFloor addFloor(Long lotId, int floorNumber, String name) {
        ParkingLot lot = getLot(lotId);
        floorRepository.findByParkingLot_IdAndFloorNumber(lotId, floorNumber)
                .ifPresent(existing -> {
                    throw new InvalidParkingStateException(
                            "Floor %d already exists in lot %d".formatted(floorNumber, lotId));
                });
        return floorRepository.save(new ParkingFloor(lot, floorNumber, name));
    }

    /**
     * Provisions a batch of spots on a floor. {@code spotCounts} maps each spot
     * type to how many to create; spot numbers are generated as
     * {@code <FLOOR>-<TYPE_PREFIX><index>}, e.g. {@code 1-C001}.
     */
    @Transactional
    public List<ParkingSpot> addSpots(Long floorId, Map<SpotType, Integer> spotCounts) {
        ParkingFloor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> ResourceNotFoundException.of("ParkingFloor", floorId));

        List<ParkingSpot> created = new ArrayList<>();
        spotCounts.forEach((type, count) -> {
            if (count == null || count <= 0) {
                return;
            }
            for (int i = 1; i <= count; i++) {
                String spotNumber = "%d-%s%03d".formatted(floor.getFloorNumber(), prefix(type), i);
                created.add(new ParkingSpot(floor, spotNumber, type));
            }
        });
        return spotRepository.saveAll(created);
    }

    private static char prefix(SpotType type) {
        return type.name().charAt(0);
    }
}

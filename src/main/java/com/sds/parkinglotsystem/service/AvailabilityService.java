package com.sds.parkinglotsystem.service;

import com.sds.parkinglotsystem.domain.enums.SpotStatus;
import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.exception.ResourceNotFoundException;
import com.sds.parkinglotsystem.repository.ParkingLotRepository;
import com.sds.parkinglotsystem.repository.ParkingSpotRepository;
import com.sds.parkinglotsystem.repository.SpotTypeCountView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

/**
 * Read-only reporting of free capacity in a lot.
 */
@Service
public class AvailabilityService {

    private final ParkingSpotRepository spotRepository;
    private final ParkingLotRepository lotRepository;

    public AvailabilityService(ParkingSpotRepository spotRepository,
                               ParkingLotRepository lotRepository) {
        this.spotRepository = spotRepository;
        this.lotRepository = lotRepository;
    }

    /**
     * @return available-spot count per spot type (types with zero free spots are
     * reported as 0 so callers see the full picture)
     */
    @Transactional(readOnly = true)
    public Map<SpotType, Long> availableByType(Long lotId) {
        if (!lotRepository.existsById(lotId)) {
            throw ResourceNotFoundException.of("ParkingLot", lotId);
        }

        Map<SpotType, Long> counts = new EnumMap<>(SpotType.class);
        for (SpotType type : SpotType.values()) {
            counts.put(type, 0L);
        }
        for (SpotTypeCountView view : spotRepository.countByStatusGroupedByType(lotId, SpotStatus.AVAILABLE)) {
            counts.put(view.getSpotType(), view.getCount());
        }
        return counts;
    }
}

package com.sds.parkinglotsystem.config;

import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.model.ParkingFloor;
import com.sds.parkinglotsystem.domain.model.ParkingLot;
import com.sds.parkinglotsystem.repository.ParkingLotRepository;
import com.sds.parkinglotsystem.service.ParkingLotAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Seeds a sample lot (one lot, two floors, a mix of spots) on first startup so
 * the API is immediately demoable. Disable with {@code parking.seed.enabled=false}.
 */
@Configuration
@ConditionalOnProperty(name = "parking.seed.enabled", havingValue = "true")
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public ApplicationRunner seedSampleLot(ParkingLotRepository lotRepository,
                                           ParkingLotAdminService adminService) {
        return args -> {
            if (lotRepository.count() > 0) {
                log.info("Parking data already present; skipping seed.");
                return;
            }

            ParkingLot lot = adminService.createLot("Downtown Garage", "1 Market Street");
            for (int floorNumber = 1; floorNumber <= 2; floorNumber++) {
                ParkingFloor floor = adminService.addFloor(lot.getId(), floorNumber, "Level " + floorNumber);
                Map<SpotType, Integer> spots = new LinkedHashMap<>();
                spots.put(SpotType.MOTORCYCLE, 5);
                spots.put(SpotType.COMPACT, 10);
                spots.put(SpotType.LARGE, 5);
                spots.put(SpotType.ELECTRIC, 3);
                spots.put(SpotType.HANDICAPPED, 2);
                adminService.addSpots(floor.getId(), spots);
            }
            log.info("Seeded sample parking lot id={} with 2 floors.", lot.getId());
        };
    }
}

package com.sds.parkinglotsystem.repository;

import com.sds.parkinglotsystem.domain.enums.SpotStatus;
import com.sds.parkinglotsystem.domain.enums.SpotType;
import com.sds.parkinglotsystem.domain.model.ParkingSpot;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    /**
     * Atomically fetches the lowest-numbered AVAILABLE spot of a given type in a
     * lot, taking a pessimistic write lock. The {@code jakarta.persistence.lock.timeout}
     * hint of {@code -2} maps to Hibernate's {@code SKIP_LOCKED}, so on Postgres
     * this issues {@code SELECT ... FOR UPDATE SKIP LOCKED}: concurrent entry
     * transactions skip rows already locked by a peer instead of blocking or
     * deadlocking, and two cars can never be handed the same last spot.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
    @Query("""
            select s from ParkingSpot s
            where s.floor.parkingLot.id = :lotId
              and s.spotType = :spotType
              and s.status = :status
            order by s.spotNumber asc
            """)
    List<ParkingSpot> findClaimableSpots(@Param("lotId") Long lotId,
                                         @Param("spotType") SpotType spotType,
                                         @Param("status") SpotStatus status,
                                         Limit limit);

    @Query("""
            select s.spotType as spotType, count(s) as count from ParkingSpot s
            where s.floor.parkingLot.id = :lotId
              and s.status = :status
            group by s.spotType
            """)
    List<SpotTypeCountView> countByStatusGroupedByType(@Param("lotId") Long lotId,
                                                        @Param("status") SpotStatus status);
}

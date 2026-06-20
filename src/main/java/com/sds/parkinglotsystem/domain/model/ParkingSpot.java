package com.sds.parkinglotsystem.domain.model;

import com.sds.parkinglotsystem.domain.enums.SpotStatus;
import com.sds.parkinglotsystem.domain.enums.SpotType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "parking_spot",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_spot_floor_number",
                columnNames = {"parking_floor_id", "spot_number"}))
@Getter
@Setter
@NoArgsConstructor
public class ParkingSpot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_floor_id", nullable = false)
    private ParkingFloor floor;

    @Column(name = "spot_number", nullable = false)
    private String spotNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type", nullable = false, length = 20)
    private SpotType spotType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SpotStatus status = SpotStatus.AVAILABLE;

    public ParkingSpot(ParkingFloor floor, String spotNumber, SpotType spotType) {
        this.floor = floor;
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.status = SpotStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return status == SpotStatus.AVAILABLE;
    }

    public void occupy() {
        this.status = SpotStatus.OCCUPIED;
    }

    public void free() {
        this.status = SpotStatus.AVAILABLE;
    }
}

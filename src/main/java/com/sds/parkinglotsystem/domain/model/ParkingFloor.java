package com.sds.parkinglotsystem.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
        name = "parking_floor",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_floor_lot_number",
                columnNames = {"parking_lot_id", "floor_number"}))
@Getter
@Setter
@NoArgsConstructor
public class ParkingFloor extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    @Column(name = "floor_number", nullable = false)
    private int floorNumber;

    @Column(nullable = false)
    private String name;

    public ParkingFloor(ParkingLot parkingLot, int floorNumber, String name) {
        this.parkingLot = parkingLot;
        this.floorNumber = floorNumber;
        this.name = name;
    }
}

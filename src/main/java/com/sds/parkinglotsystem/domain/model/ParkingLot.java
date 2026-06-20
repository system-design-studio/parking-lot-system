package com.sds.parkinglotsystem.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parking_lot")
@Getter
@Setter
@NoArgsConstructor
public class ParkingLot extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    public ParkingLot(String name, String address) {
        this.name = name;
        this.address = address;
    }
}

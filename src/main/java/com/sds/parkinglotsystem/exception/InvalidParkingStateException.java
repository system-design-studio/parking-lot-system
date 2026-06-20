package com.sds.parkinglotsystem.exception;

/**
 * Thrown when an operation is invalid for the current state, e.g. paying for a
 * ticket that is not awaiting payment, or parking a vehicle that is already
 * parked. Maps to HTTP 409 (Conflict).
 */
public class InvalidParkingStateException extends RuntimeException {

    public InvalidParkingStateException(String message) {
        super(message);
    }
}

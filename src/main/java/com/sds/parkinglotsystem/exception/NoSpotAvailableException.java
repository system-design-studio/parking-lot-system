package com.sds.parkinglotsystem.exception;

/**
 * Thrown when no compatible spot is free for an incoming vehicle.
 * Maps to HTTP 409 (Conflict).
 */
public class NoSpotAvailableException extends RuntimeException {

    public NoSpotAvailableException(String message) {
        super(message);
    }
}

package com.sds.parkinglotsystem.domain.enums;

import java.util.Set;

/**
 * Lifecycle of a parking ticket:
 * ACTIVE -> AWAITING_PAYMENT -> PAID, with LOST as a terminal exception path.
 */
public enum TicketStatus {
    ACTIVE,
    AWAITING_PAYMENT,
    PAID,
    LOST;

    /** Statuses that mean the vehicle is still occupying a spot. */
    public static final Set<TicketStatus> OPEN_STATUSES = Set.of(ACTIVE, AWAITING_PAYMENT);
}

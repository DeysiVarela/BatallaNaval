package com.deysi.battleship.model;

/**
 * Legacy naming kept for clarity in tests and UI labels.
 */
public enum ShotOutcome {
    /** Shot landed in water. */
    WATER,
    /** Shot hit a ship segment. */
    HIT,
    /** Shot sank the impacted ship. */
    SUNK
}

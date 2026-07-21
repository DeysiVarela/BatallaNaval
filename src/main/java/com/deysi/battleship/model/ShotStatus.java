package com.deysi.battleship.model;

/**
 * Current shot mark in the board.
 */
public enum ShotStatus {
    /** Cell has not been shot yet. */
    UNKNOWN,
    /** Shot landed in water. */
    WATER,
    /** Shot hit a ship segment. */
    HIT,
    /** Shot sank the impacted ship. */
    SUNK
}

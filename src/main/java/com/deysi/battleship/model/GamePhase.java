package com.deysi.battleship.model;

/**
 * Game lifecycle.
 */
public enum GamePhase {
    /** Player is still placing ships. */
    PLACING,
    /** Battle is active and turns are being played. */
    PLAYING,
    /** Match finished after one side lost all ships. */
    FINISHED
}

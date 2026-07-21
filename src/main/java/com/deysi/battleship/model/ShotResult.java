package com.deysi.battleship.model;

/**
 * Result of a shot.
 *
 * @param coordinate target coordinate used in the shot
 * @param status outcome of the shot
 * @param ship ship affected by the shot, or null for water
 */
public record ShotResult(Coordinate coordinate, ShotStatus status, Ship ship) {
}

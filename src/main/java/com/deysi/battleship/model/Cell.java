package com.deysi.battleship.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * A single cell inside the board.
 */
public class Cell implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Fixed coordinate represented by this cell. */
    private final Coordinate coordinate;
    /** Ship reference occupying this cell, if any. */
    private Ship ship;
    /** Current shot status visualized in the board. */
    private ShotStatus shotStatus = ShotStatus.UNKNOWN;

    /**
     * Creates a board cell for a fixed coordinate.
     *
     * @param coordinate cell coordinate
     */
    public Cell(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * Returns the immutable coordinate assigned to this cell.
     *
     * @return cell coordinate
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Checks whether any ship occupies this cell.
     *
     * @return true when a ship occupies the cell
     */
    public boolean hasShip() {
        return ship != null;
    }

    /**
     * Returns the ship currently occupying this cell, if any.
     *
     * @return occupying ship or null
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Sets the occupying ship reference.
     *
     * @param ship occupying ship
     */
    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * Returns the current visual state associated with shots.
     *
     * @return current shot status for this cell
     */
    public ShotStatus getShotStatus() {
        return shotStatus;
    }

    /**
     * Updates shot status for this cell.
     *
     * @param shotStatus new status
     */
    public void setShotStatus(ShotStatus shotStatus) {
        this.shotStatus = shotStatus;
    }
}

package com.deysi.battleship.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A ship with a fixed set of coordinates.
 */
public class Ship implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Ship catalog type. */
    private final ShipType type;
    /** Orientation used to expand coordinates from origin. */
    private final Orientation orientation;
    /** Coordinates occupied by the ship. */
    private final List<Coordinate> coordinates;
    /** Coordinates already hit by shots. */
    private final Set<Coordinate> hits = new HashSet<>();

    /**
     * Creates a ship from type, origin, and orientation.
     *
     * @param type ship type
     * @param origin origin coordinate
     * @param orientation orientation used to expand coordinates
     */
    public Ship(ShipType type, Coordinate origin, Orientation orientation) {
        this.type = type;
        this.orientation = orientation;
        this.coordinates = buildCoordinates(origin, type.size(), orientation);
    }

    /**
     * Returns the ship type metadata.
     *
     * @return ship type
     */
    public ShipType type() {
        return type;
    }

    /**
     * Returns the orientation assigned at construction.
     *
     * @return ship orientation
     */
    public Orientation orientation() {
        return orientation;
    }

    /**
     * Returns all board coordinates occupied by this ship.
     *
     * @return immutable list of occupied coordinates
     */
    public List<Coordinate> coordinates() {
        return Collections.unmodifiableList(coordinates);
    }

    /**
     * Registers a hit on one coordinate of the ship.
     *
     * @param coordinate impacted coordinate
     */
    public void registerHit(Coordinate coordinate) {
        hits.add(coordinate);
    }

    /**
     * Checks whether the ship occupies the given coordinate.
     *
     * @param coordinate coordinate to test
     * @return true when coordinate is part of the ship
     */
    public boolean occupies(Coordinate coordinate) {
        return coordinates.contains(coordinate);
    }

    /**
     * Indicates whether all ship coordinates were hit.
     *
     * @return true when ship is sunk
     */
    public boolean isSunk() {
        return hits.size() >= coordinates.size();
    }

    private List<Coordinate> buildCoordinates(Coordinate origin, int size, Orientation orientation) {
        List<Coordinate> builtCoordinates = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            builtCoordinates.add(orientation == Orientation.HORIZONTAL
                    ? new Coordinate(origin.row(), origin.col() + index)
                    : new Coordinate(origin.row() + index, origin.col()));
        }
        return builtCoordinates;
    }
}

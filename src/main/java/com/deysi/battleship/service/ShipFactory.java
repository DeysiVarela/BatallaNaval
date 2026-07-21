package com.deysi.battleship.service;

import com.deysi.battleship.model.Coordinate;
import com.deysi.battleship.model.Orientation;
import com.deysi.battleship.model.Ship;
import com.deysi.battleship.model.ShipType;

/**
 * Creates ships using a single construction path.
 */
public class ShipFactory {

    /**
     * Creates a ship factory.
     */
    public ShipFactory() {
        // Default constructor.
    }

    /**
     * Builds a ship instance from explicit placement data.
     *
     * @param shipType ship category
     * @param origin start coordinate
     * @param orientation horizontal or vertical
     * @return constructed ship
     */
    public Ship create(ShipType shipType, Coordinate origin, Orientation orientation) {
        return new Ship(shipType, origin, orientation);
    }
}

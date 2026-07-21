package com.deysi.battleship.model;

import com.deysi.battleship.exception.InvalidPlacementException;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;

/**
 * Serializable 10x10 board with ships and shots.
 */
public class Board implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Fixed board size (10x10).
     */
    public static final int SIZE = 10;

    /** Matrix of board cells indexed by row and column. */
    private final Cell[][] cells = new Cell[SIZE][SIZE];
    /** Ships currently placed on the board. */
    private final List<Ship> ships = new ArrayList<>();
    /** Coordinates that have already been shot. */
    private final Set<Coordinate> shots = new HashSet<>();
    /** Placed ships grouped by type to enforce quantity limits. */
    private final EnumMap<ShipType, Integer> shipsByType = new EnumMap<>(ShipType.class);

    /**
     * Creates an empty board with initialized cells.
     */
    public Board() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new Cell(new Coordinate(row, col));
            }
        }
    }

    /**
     * Returns the cell at the provided coordinate.
     *
     * @param coordinate board coordinate
     * @return cell instance
     */
    public Cell getCell(Coordinate coordinate) {
        validateBounds(coordinate);
        return cells[coordinate.row()][coordinate.col()];
    }

    /**
     * Returns an immutable view of all placed ships.
     *
     * @return placed ships
     */
    public List<Ship> getShips() {
        return Collections.unmodifiableList(ships);
    }

    /**
     * Returns an immutable view of all shot coordinates.
     *
     * @return fired shot coordinates
     */
    public Set<Coordinate> getShots() {
        return Collections.unmodifiableSet(shots);
    }

    /**
     * Places a ship in the board when placement rules are satisfied.
     *
     * @param ship ship to place
     * @throws InvalidPlacementException when position overlaps, exceeds bounds or quota
     */
    public void placeShip(Ship ship) throws InvalidPlacementException {
        if (!canPlaceShip(ship)) {
            throw new InvalidPlacementException("The selected ship cannot be placed there.");
        }

        for (Coordinate coordinate : ship.coordinates()) {
            validateBounds(coordinate);
        }

        ships.add(ship);
        shipsByType.merge(ship.type(), 1, Integer::sum);
        for (Coordinate coordinate : ship.coordinates()) {
            cells[coordinate.row()][coordinate.col()].setShip(ship);
        }
    }

    /**
     * Applies a shot to the target coordinate and returns its result.
     *
     * @param coordinate target coordinate
     * @return shot result for the cell
     * @throws InvalidPlacementException when coordinate was already shot
     */
    public ShotResult receiveShot(Coordinate coordinate) throws InvalidPlacementException {
        validateBounds(coordinate);
        if (shots.contains(coordinate)) {
            throw new InvalidPlacementException("This cell has already been shot.");
        }

        shots.add(coordinate);
        Cell cell = cells[coordinate.row()][coordinate.col()];
        if (!cell.hasShip()) {
            cell.setShotStatus(ShotStatus.WATER);
            return new ShotResult(coordinate, ShotStatus.WATER, null);
        }

        Ship ship = cell.getShip();
        ship.registerHit(coordinate);
        if (ship.isSunk()) {
            ship.coordinates().forEach(hitCoordinate -> cells[hitCoordinate.row()][hitCoordinate.col()].setShotStatus(ShotStatus.SUNK));
            return new ShotResult(coordinate, ShotStatus.SUNK, ship);
        }

        cell.setShotStatus(ShotStatus.HIT);
        return new ShotResult(coordinate, ShotStatus.HIT, ship);
    }

    /**
     * Indicates whether all currently placed ships are sunk.
     *
     * @return true when every placed ship is sunk
     */
    public boolean allShipsSunk() {
        return !ships.isEmpty() && ships.stream().allMatch(Ship::isSunk);
    }

    /**
     * Indicates whether the player has placed the complete default fleet.
     *
     * @return true when fleet size matches catalog quantity
     */
    public boolean isFleetComplete() {
        return ships.size() == ShipType.totalFleetCount();
    }

    /**
     * Returns the placement completion ratio for the default fleet.
     *
     * @return value in range [0,1]
     */
    public double getFleetCompletionRatio() {
        if (ShipType.totalFleetCount() == 0) {
            return 0;
        }
        return ships.size() / (double) ShipType.totalFleetCount();
    }

    /**
     * Validates whether a ship can be placed in its current coordinates.
     *
     * @param ship ship to validate
     * @return true when placement is legal
     */
    public boolean canPlaceShip(Ship ship) {
        int placedCount = shipsByType.getOrDefault(ship.type(), 0);
        if (placedCount >= ship.type().quantity()) {
            return false;
        }

        for (Coordinate coordinate : ship.coordinates()) {
            if (!coordinate.isInsideBoard()) {
                return false;
            }
            if (cells[coordinate.row()][coordinate.col()].hasShip()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns how many ships of the specified type have been placed.
     *
     * @param shipType ship type
     * @return placed count
     */
    public int getPlacedCount(ShipType shipType) {
        return shipsByType.getOrDefault(shipType, 0);
    }

    private void validateBounds(Coordinate coordinate) {
        if (!coordinate.isInsideBoard()) {
            throw new IllegalArgumentException("Coordinate out of bounds: " + coordinate);
        }
    }
}

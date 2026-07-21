package com.deysi.battleship.model;

import com.deysi.battleship.exception.InvalidPlacementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    @Test
    void placeShipShouldRejectOverlap() throws InvalidPlacementException {
        Board board = new Board();
        board.placeShip(new Ship(ShipType.DESTROYER, new Coordinate(0, 0), Orientation.HORIZONTAL));

        assertThrows(InvalidPlacementException.class,
                () -> board.placeShip(new Ship(ShipType.FRIGATE, new Coordinate(0, 1), Orientation.HORIZONTAL)));
    }

    @Test
    void receiveShotShouldMarkWaterAndPreventDuplicateShots() throws InvalidPlacementException {
        Board board = new Board();

        ShotResult first = board.receiveShot(new Coordinate(4, 4));

        assertEquals(ShotStatus.WATER, first.status());
        assertThrows(InvalidPlacementException.class, () -> board.receiveShot(new Coordinate(4, 4)));
    }

    @Test
    void receiveShotShouldSunkShipWhenAllCellsAreHit() throws InvalidPlacementException {
        Board board = new Board();
        Ship ship = new Ship(ShipType.FRIGATE, new Coordinate(2, 2), Orientation.HORIZONTAL);
        board.placeShip(ship);

        ShotResult result = board.receiveShot(new Coordinate(2, 2));

        assertEquals(ShotStatus.SUNK, result.status());
        assertTrue(ship.isSunk());
    }

    @Test
    void placeShipShouldRejectExceedingShipTypeQuantity() throws InvalidPlacementException {
        Board board = new Board();

        board.placeShip(new Ship(ShipType.FRIGATE, new Coordinate(0, 0), Orientation.HORIZONTAL));
        board.placeShip(new Ship(ShipType.FRIGATE, new Coordinate(0, 1), Orientation.HORIZONTAL));
        board.placeShip(new Ship(ShipType.FRIGATE, new Coordinate(0, 2), Orientation.HORIZONTAL));
        board.placeShip(new Ship(ShipType.FRIGATE, new Coordinate(0, 3), Orientation.HORIZONTAL));

        assertThrows(InvalidPlacementException.class,
                () -> board.placeShip(new Ship(ShipType.FRIGATE, new Coordinate(0, 4), Orientation.HORIZONTAL)));
    }
}

package com.deysi.battleship.service;

import com.deysi.battleship.model.Board;
import com.deysi.battleship.model.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Pure random shot strategy.
 */
public class RandomShotStrategy implements ShotStrategy {

    private final Random random = new Random();

    /**
     * Creates a random machine-shot strategy.
     */
    public RandomShotStrategy() {
        // Default constructor.
    }

    /**
     * Returns a random not-yet-shot coordinate.
     *
     * @param board target board
     * @return next coordinate to shoot
     */
    @Override
    public Coordinate nextShot(Board board) {
        List<Coordinate> availableCoordinates = new ArrayList<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Coordinate coordinate = new Coordinate(row, col);
                if (!board.getShots().contains(coordinate)) {
                    availableCoordinates.add(coordinate);
                }
            }
        }
        return availableCoordinates.get(random.nextInt(availableCoordinates.size()));
    }
}

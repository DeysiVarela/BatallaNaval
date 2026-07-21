package com.deysi.battleship.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * Zero-based board coordinate.
 *
 * @param row zero-based row index
 * @param col zero-based column index
 */
public record Coordinate(int row, int col) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Checks whether the coordinate is inside board limits.
     *
     * @return true when row and column are valid indexes
     */
    public boolean isInsideBoard() {
        return row >= 0 && row < Board.SIZE && col >= 0 && col < Board.SIZE;
    }
}

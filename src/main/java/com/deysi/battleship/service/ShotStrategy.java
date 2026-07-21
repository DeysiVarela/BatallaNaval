package com.deysi.battleship.service;

import com.deysi.battleship.model.Board;
import com.deysi.battleship.model.Coordinate;

/**
 * Strategy for machine target selection.
 */
public interface ShotStrategy {

    /**
     * Picks the next coordinate for the machine turn.
     *
     * @param board target board to inspect
     * @return coordinate to shoot
     */
    Coordinate nextShot(Board board);
}

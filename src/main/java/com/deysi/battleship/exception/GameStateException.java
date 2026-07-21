package com.deysi.battleship.exception;

/**
 * Signals an invalid action in the current game phase.
 */
public class GameStateException extends RuntimeException {

    /**
     * Creates an exception with message describing invalid game-state action.
     *
     * @param message error message
     */
    public GameStateException(String message) {
        super(message);
    }
}

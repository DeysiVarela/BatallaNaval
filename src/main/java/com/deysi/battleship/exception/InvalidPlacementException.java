package com.deysi.battleship.exception;

/**
 * Signals a ship placement that breaks board rules.
 */
public class InvalidPlacementException extends Exception {

    /**
     * Creates an exception with message describing invalid board placement.
     *
     * @param message error message
     */
    public InvalidPlacementException(String message) {
        super(message);
    }
}

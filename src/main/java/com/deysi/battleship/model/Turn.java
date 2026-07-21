package com.deysi.battleship.model;

/**
 * Current turn owner.
 */
public enum Turn {
    /** Human player turn. */
    PLAYER,
    /** Machine turn. */
    MACHINE;

    /**
     * Returns the other turn owner.
     *
     * @return opposite turn
     */
    public Turn opposite() {
        return this == PLAYER ? MACHINE : PLAYER;
    }
}

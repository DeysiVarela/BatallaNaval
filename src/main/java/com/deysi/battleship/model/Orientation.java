package com.deysi.battleship.model;

/**
 * Ship placement orientation.
 */
public enum Orientation {
    /** Ship grows on columns from origin. */
    HORIZONTAL,
    /** Ship grows on rows from origin. */
    VERTICAL;

    /**
     * Returns the opposite orientation.
     *
     * @return opposite orientation
     */
    public Orientation opposite() {
        return this == HORIZONTAL ? VERTICAL : HORIZONTAL;
    }
}

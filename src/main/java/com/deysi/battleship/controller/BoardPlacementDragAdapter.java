package com.deysi.battleship.controller;

import com.deysi.battleship.model.Coordinate;
import com.deysi.battleship.view.BoardRenderer;

/**
 * Adapter class for board placement drag events.
 *
 * <p>Subclasses override only the callbacks they need.
 */
public class BoardPlacementDragAdapter implements BoardRenderer.PlacementDragListener {

    /**
     * Creates a no-op placement drag adapter.
     */
    public BoardPlacementDragAdapter() {
        // Default adapter constructor.
    }

    @Override
    public void onDragEntered(Coordinate coordinate) {
        // Adapter default implementation.
    }

    @Override
    public void onDragExited(Coordinate coordinate) {
        // Adapter default implementation.
    }

    @Override
    public void onDrop(Coordinate coordinate) {
        // Adapter default implementation.
    }
}

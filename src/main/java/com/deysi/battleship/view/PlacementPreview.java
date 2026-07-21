package com.deysi.battleship.view;

import com.deysi.battleship.model.Coordinate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Visual preview for a ship placement during drag-and-drop.
 */
public record PlacementPreview(Set<Coordinate> coordinates, boolean valid) {

    public PlacementPreview {
        coordinates = Collections.unmodifiableSet(new HashSet<>(coordinates));
    }

    public static PlacementPreview empty() {
        return new PlacementPreview(Set.of(), false);
    }

    public boolean contains(Coordinate coordinate) {
        return coordinates.contains(coordinate);
    }

    public boolean isActive() {
        return !coordinates.isEmpty();
    }
}

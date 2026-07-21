package com.deysi.battleship.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ship catalog.
 */
public enum ShipType {
    /** Carrier ship occupying 4 cells (1 unit). */
    CARRIER("Carrier", 4, 1),
    /** Submarine ship occupying 3 cells (2 units). */
    SUBMARINE("Submarine", 3, 2),
    /** Destroyer ship occupying 2 cells (3 units). */
    DESTROYER("Destroyer", 2, 3),
    /** Frigate ship occupying 1 cell (4 units). */
    FRIGATE("Frigate", 1, 4);

    private final String displayName;
    private final int size;
    private final int quantity;

    ShipType(String displayName, int size, int quantity) {
        this.displayName = displayName;
        this.size = size;
        this.quantity = quantity;
    }

    /**
     * Returns the localized display name used by the UI.
     *
     * @return human-readable ship name
     */
    public String displayName() {
        return displayName;
    }

    /**
     * Returns the ship length measured in board cells.
     *
     * @return amount of cells occupied by this type
     */
    public int size() {
        return size;
    }

    /**
     * Returns how many units of this type exist in the default fleet.
     *
     * @return amount of ships of this type in the default fleet
     */
    public int quantity() {
        return quantity;
    }

    /**
     * Returns total ships in the default fleet configuration.
     *
     * @return total fleet size
     */
    public static int totalFleetCount() {
        return 10;
    }

    /**
     * Expands enum configuration into one list entry per ship unit.
     *
     * @return immutable default fleet list
     */
    public static List<ShipType> defaultFleet() {
        List<ShipType> fleet = new ArrayList<>();
        for (ShipType shipType : values()) {
            for (int index = 0; index < shipType.quantity; index++) {
                fleet.add(shipType);
            }
        }
        return Collections.unmodifiableList(fleet);
    }

    /**
     * Resolves ship type from text shown in the UI.
     *
     * @param text display text containing ship label
     * @return matching ship type
     */
    public static ShipType fromDisplayText(String text) {
        for (ShipType shipType : values()) {
            if (text != null && text.startsWith(shipType.displayName)) {
                return shipType;
            }
        }
        throw new IllegalArgumentException("Unknown ship type: " + text);
    }
}

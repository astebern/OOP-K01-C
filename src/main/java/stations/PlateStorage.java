package stations;

import items.equipment.Plate;
import java.util.Stack;

public class PlateStorage extends Station {
    private Stack<Plate> plates;
    private static final int INITIAL_PLATE_COUNT = 4;

    public PlateStorage() {
        this.plates = new Stack<>();
        // Initialize with 4 clean plates
        for (int i = 0; i < INITIAL_PLATE_COUNT; i++) {
            plates.push(Plate.createClean());
        }
    }

    /**
     * Checks if there are plates available in storage
     */
    public boolean hasPlates() {
        return !plates.isEmpty();
    }

    public Stack<Plate> getPlates() {
        return plates;
    }
    /**
     * Gets the number of plates currently in storage
     */
    public int getPlateCount() {
        return plates.size();
    }

    /**
     * Takes a plate from storage (for pickup)
     * @return The plate, or null if storage is empty
     */
    public Plate takePlate() {
        if (hasPlates()) {
            return plates.pop();
        }
        return null;
    }

    /**
     * Returns a plate to storage (for drop)
     * @param plate The plate to return
     * @return true if plate was successfully stored
     */
    public boolean storePlate(Plate plate) {
        if (plate != null) {
            plates.push(plate);
            return true;
        }
        return false;
    }
}


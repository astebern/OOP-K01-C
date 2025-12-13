package stations;

import items.equipment.Plate;
import utils.BetterComments;

import java.util.Stack;

@BetterComments(description = "Static station that holds clean plates after washing. Cannot be interacted with directly.", type = "class")
public class CleanPlateStation extends Station {
    private Stack<Plate> cleanPlates;
    private static final int MAX_PLATES = 10;

    public CleanPlateStation() {
        this.cleanPlates = new Stack<>();
    }

    @BetterComments(description = "Adds a clean plate to the stack", type = "method")
    public boolean addCleanPlate(Plate plate) {
        if (cleanPlates.size() < MAX_PLATES && !plate.getIsDirty()) {
            cleanPlates.push(plate);
            return true;
        }
        return false;
    }

    @BetterComments(description = "Removes a clean plate from the stack (called by chef when picking up)", type = "method")
    public Plate removeCleanPlate() {
        if (!cleanPlates.isEmpty()) {
            return cleanPlates.pop();
        }
        return null;
    }

    @BetterComments(description = "Checks if there are clean plates available", type = "method")
    public boolean hasCleanPlates() {
        return !cleanPlates.isEmpty();
    }

    @BetterComments(description = "Gets the stack of clean plates for rendering", type = "method")
    public Stack<Plate> getCleanPlates() {
        return cleanPlates;
    }

    @BetterComments(description = "Gets the number of clean plates", type = "method")
    public int getPlateCount() {
        return cleanPlates.size();
    }

    @BetterComments(description = "Checks if the station is full", type = "method")
    public boolean isFull() {
        return cleanPlates.size() >= MAX_PLATES;
    }

    @Override
    public String toString() {
        return "CleanPlateStation - Clean Plates: " + cleanPlates.size();
    }
}


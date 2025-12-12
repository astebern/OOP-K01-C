package stations;

import items.Item;
import items.equipment.Plate;

public class TrashStation extends Station {
    
    /**
     * Disposes an item in the trash.
     * - If it's a Plate: only clears the contents (plate is reusable)
     * - If it's anything else: item is deleted (returns true to indicate deletion)
     *
     * @param item The item to dispose
     * @return true if item should be deleted from inventory, false if item should remain (plate)
     */
    public boolean disposeItem(Item item) {
        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            if (!plate.getContents().isEmpty()) {
                System.out.println("TrashStation: Cleared contents from plate");
                plate.clearContents();
                plate.setIsDirty(true); // Mark plate as dirty after clearing
            } else {
                System.out.println("TrashStation: Plate is already empty");
            }
            return false; // Keep the plate in inventory
        } else {
            // Delete any other item (ingredients, utensils, etc.)
            System.out.println("TrashStation: Deleted " + item.getClass().getSimpleName());
            return true; // Remove item from inventory
        }
    }
}


package stations;

import items.Item;
import items.equipment.Plate;

public class TrashStation extends Station {
    

    public boolean disposeItem(Item item) {
        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            if (!plate.getContents().isEmpty()) {
                System.out.println("TrashStation: Cleared item from plate");
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


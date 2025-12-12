package items.equipment;

import items.Preparable;
import items.food.Ingredient;
import items.food.IngredientState;
import java.util.ArrayList;
import java.util.List;

public class FryingPan extends KitchenUtensil implements CookingDevice {
    private int capacity;
    private List<String> acceptableNames;
    private boolean isCooking;

    public FryingPan() {
        super(); 
        this.capacity = 1; 
        this.isCooking = false;

        // Ingredients that can be fried
        this.acceptableNames = new ArrayList<>();
        acceptableNames.add("Daging");
        acceptableNames.add("Ikan");
        acceptableNames.add("Udang");
        acceptableNames.add("Tomat");
    }

    @Override
    public boolean isPortable() {
        return false; // FryingPan cannot be picked up
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean canAccept(Preparable item) {
        if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;

            if (ing.getState() != IngredientState.CHOPPED) {
                return false;
            }

            for (String validName : acceptableNames) {
                if (validName.equalsIgnoreCase(ing.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void addIngredient(Preparable item) {
        if (contents.size() < capacity && canAccept(item)) {
            super.addContent(item);
        } else {
            System.out.println("Frying Pan refused item: " + item);
        }
    }

    @Override
    public void removeIngredient(Preparable item) {
        if (contents.contains(item)) {
            contents.remove(item);
        }
    }

    @Override
    public void startCooking() {
        if (!contents.isEmpty()) {
            this.isCooking = true;
            System.out.println("Frying Pan started cooking...");
        }
    }

    @Override
    public void addContent(Preparable item) {
        addIngredient(item);
    }
    
    public boolean isCooking() {
        return isCooking;
    }

    public void setCooking(boolean cooking) {
        this.isCooking = cooking;
    }
}
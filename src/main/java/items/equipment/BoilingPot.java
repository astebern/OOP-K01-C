package items.equipment;

import items.Preparable;
import items.food.Ingredient;
import items.food.IngredientState;
import java.util.ArrayList;
import java.util.List;

public class BoilingPot extends KitchenUtensil implements CookingDevice {
    private int capacity;
    private boolean portable;
    private List<String> acceptableNames; 
    private boolean isCooking; 

    public BoilingPot() {
        super(); 
        this.capacity = 1; 
        this.portable = true; 
        this.isCooking = false;

        // Ingredients that can be boiled
        this.acceptableNames = new ArrayList<>();
        acceptableNames.add("Pasta");
        acceptableNames.add("Rice");
        acceptableNames.add("Potato");
    }

    @Override
    public boolean isPortable() {
        return portable;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean canAccept(Preparable item) {
        if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            
            if (ing.getState() != IngredientState.RAW) {
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
        } else {
            System.out.println("Cannot add item: Pot is full or item is invalid.");
        }
    }

    @Override
    public void removeIngredient(Preparable item) {
        // Using contents from parent class
        if (contents.contains(item)) {
            contents.remove(item);
        }
    }

    @Override
    public void startCooking() {
        if (!contents.isEmpty()) {
            this.isCooking = true;
            System.out.println("Boiling Pot started cooking...");
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
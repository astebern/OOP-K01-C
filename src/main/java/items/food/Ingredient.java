package items.food;

import items.Item;
import items.Preparable;

public class Ingredient extends Item implements Preparable {
    private String name;
    private IngredientState state;
    
    private boolean choppable;
    private boolean cookable;
    private boolean canPlaceOnPlate;

    public Ingredient(String name) {
        this.name = name; 
        this.state = IngredientState.RAW;
        
        // Default logic awal
        this.choppable = true;
        this.cookable = true;
        this.canPlaceOnPlate = false; 
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    public void setState(IngredientState newState) {
        this.state = newState;
    }

    public IngredientState getState() {
        return state;
    }

    public void setChoppable(boolean bol) {
        this.choppable = bol;
    }

    public void setCookable(boolean bol) {
        this.cookable = bol;
    }

    public void setcanPlaceOnPlate(boolean bol) {
        this.canPlaceOnPlate = bol;
    }


    @Override
    public boolean canBeChopped() {
        return this.choppable;
    }

    @Override
    public boolean canBeCooked() {
        return this.cookable;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return this.canPlaceOnPlate;
    }

    @Override
    public boolean chop() {
        if (this.choppable) {
            this.state = IngredientState.CHOPPED;
            this.choppable = false; 
            this.cookable = true; 
            return true;
        }
        return false;
    }

    @Override
    public boolean cook() {
        if (this.cookable) {
            this.state = IngredientState.COOKED;
            this.cookable = false;
            this.choppable = false;
            this.canPlaceOnPlate = true;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name + " [" + state + "]";
    }
}
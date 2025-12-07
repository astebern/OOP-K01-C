package items.food;

import items.Item;
import items.Preparable;

public class Ingredient extends Item implements Preparable {
    private IngredientState state;

    public Ingredient(String name) {
        super(name);
        this.state = IngredientState.RAW;
    }

    public IngredientState getState() {
        return state;
    }

    public void setState(IngredientState state) {
        this.state = state;
    }

    @Override
    public boolean canBeChopped() {
        return this.state == IngredientState.RAW;
    }

    @Override
    public boolean canBeCooked() {
        return this.state == IngredientState.RAW || this.state == IngredientState.CHOPPED;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return this.state != IngredientState.BURNED;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
            System.out.println(name + " dipotong menjadi CHOPPED.");
        }
    }

    @Override
    public void cook() {
        this.state = IngredientState.COOKED;
    }
    
    // Method helper untuk logic hangus
    public void burn() {
        this.state = IngredientState.BURNED;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + state + ")";
    }
}
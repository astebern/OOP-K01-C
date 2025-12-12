package items.food;

import items.Item;
import items.Preparable;

public class Ingredient extends Item implements Preparable {
    private String name;            
    private IngredientState state;

    private Ingredient(String name) {
        this.name = name;
        this.state = IngredientState.RAW;
    }

    public static Ingredient create(String name) {
        return new Ingredient(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IngredientState getState() {
        return state;
    }

    public void setState(IngredientState state) {
        this.state = state;
    }

    @Override
    public boolean canBeChopped() {
        return state == IngredientState.RAW;
    }

    @Override
    public boolean canBeCooked() {
        return state == IngredientState.RAW || state == IngredientState.CHOPPED;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return state != IngredientState.RAW && state != IngredientState.BURNED;
    }

    @Override
    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
        }
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            this.state = IngredientState.COOKED;
        }
    }

    @Override
    public void setChoppable(boolean choppable) {}

    @Override
    public void setCookable(boolean cookable) {}

    @Override
    public void setCanBePlacedOnPlate(boolean canPlace) {}

    @Override
    public String toString() {
        return name + " [" + state + "]";
    }
}
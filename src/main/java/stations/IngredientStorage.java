package stations;


import java.util.ArrayList;
import java.util.List;

import entities.Chef;
import items.Item;
import items.Preparable;
import items.food.Ingredient;
import utils.Position;

public class IngredientStorage extends Station {

    private String ingredientType;
    private List<Preparable> contents = new ArrayList<>();

    public IngredientStorage(Position position, String ingredientType) {
        super(position);
        this.ingredientType = ingredientType;
    }

    public String getIngredientType() {
        return ingredientType;
    }

    public void setIngredientType(String ingredientType) {
        this.ingredientType = ingredientType;
    }

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        Item topItem = getTopItemOnThisStation();

        if (topItem != null) {
                if (carried == null) {
                    chef.setInventory(topItem);
                    removeTopItemThisStation();
                    return;
                }

            if (canDoPlating(carried, topItem)) {
                Item platedItem = doPlating(carried, topItem);
                removeTopItemThisStation();
                setTopItemOnThisStation(platedItem);
                chef.setInventory(null);
        
                return;
            }

            return;
        }

        if (carried == null) {
                Ingredient ingredient = Ingredient.create(ingredientType);
                chef.setInventory(ingredient);
            } else {
                setTopItemOnThisStation(carried);
                chef.setInventory(null);
        
        }


       
    }

    protected Item getTopItemOnThisStation() {
        return null; 
    }
    protected void setTopItemOnThisStation(Item item) {
    }
    protected void removeTopItemThisStation() {
    }
    protected boolean canDoPlating(Item carried, Item topItem) {
        return false; 
    }
    protected Item doPlating(Item carried, Item topItem) {
        return null; 
  }

}

    



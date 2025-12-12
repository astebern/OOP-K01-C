package stations;


import entities.Chef;
import items.Item;
import items.Preparable;
import items.equipment.KitchenUtensil;
import items.food.Ingredient;
import items.food.IngredientState;
import utils.Position;



public class CookingStation extends Station {

    private KitchenUtensil utensilOnBoard;

    private boolean isActive;
    private int progress;

    private final int COOKING_TIME = 100;
    private final int BURN_TIME = 160;

    public CookingStation(Position position) {
        super(position);
        this.utensilOnBoard = null; 
        this.isActive = false;
        this.progress = 0;
    }

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        if (utensilOnBoard != null) {
            if (carried == null) {
                chef.setInventory(utensilOnBoard);
                utensilOnBoard = null;
                isActive = false;
                progress = 0;

            }

            return;
        }

        if (carried instanceof KitchenUtensil) {
            KitchenUtensil ku = (KitchenUtensil) carried;

        if (hasCookableContent(ku)) {
                utensilOnBoard = ku;
                chef.setInventory(null);

                isActive = true;
                progress = 0;

                System.out.println("Cooking started on CookingStation.");
            } else {
                System.out.println("No cookable content in utensil.");
            }
        }
    }
    

    private boolean hasCookableContent(KitchenUtensil ku) {
        for (Preparable p : ku.getContents()) {
            if (p.canBeCooked()) {
                    return true;
                }
            }
        
        return false;
    }

    public void updateCooking(){
        if (!isActive || utensilOnBoard == null) {
            return;
        }

        progress ++;

        

        if (progress == COOKING_TIME) {
            System.out.println("Cooking Station : Cooking completed.");
            for (Preparable p : utensilOnBoard.getContents()) {
                if (p.canBeCooked()) {
                    p.cook();
                }
            }
        }
        if (progress > BURN_TIME) {
            System.out.println("Cooking Station : Food burned!");
            for (Preparable p : utensilOnBoard.getContents()) {
                if (p instanceof Ingredient) {
                    ((Ingredient) p).setState(IngredientState.BURNED);
                }
            }
            isActive = false; 
        }
    }
    

    public KitchenUtensil getUtensilOnBoard() {
        return utensilOnBoard;
    }
    public boolean isActive() {
        return isActive;
    }
    public int getProgress() {
        return progress;

    }
}


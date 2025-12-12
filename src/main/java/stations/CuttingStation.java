package stations;


import entities.Chef;
import items.Item;
import items.food.Ingredient;
import utils.Position;


        

public class CuttingStation extends Station {

    private Ingredient ingredientOnBoard;
    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int newProgress) {
        this.progress = newProgress;
    }



    public CuttingStation(Position position) {
        super(position);
        this.progress = 0;
    }

    @Override
    public void interact(Chef chef) {
        Item carried = chef.getInventory();

        if (ingredientOnBoard != null) {
            if (carried == null) {
                chef.setInventory(ingredientOnBoard);
                ingredientOnBoard = null;
                return;
            }


            return;
        }

        if (carried instanceof Ingredient) {
            Ingredient ing = (Ingredient) carried;

            if (ing.canBeChopped()) {
                chef.setInventory(null);
                ingredientOnBoard = ing;
                ingredientOnBoard.chop();
                return;
            } else {
                return;
            }
        }
    }

    public Ingredient getIngredientOnBoard() {
        return ingredientOnBoard;
    }

    public void setIngredientOnBoard(Ingredient ingredientOnBoard) {
        this.ingredientOnBoard = ingredientOnBoard;
    }

    
}



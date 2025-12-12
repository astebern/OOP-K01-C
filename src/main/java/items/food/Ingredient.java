package items.food;

import items.Item;
import items.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Ingredient extends Item implements Preparable {
    private String name;            
    private IngredientState state;

    private Ingredient(String name) {
        this.name = name;
        this.state = IngredientState.RAW;
        loadImage();
    }

    public static Ingredient create(String name) {
        return new Ingredient(name);
    }

    private void loadImage() {
        try {
            String nameLower = name.toLowerCase();
            String stateStr = "";

            switch (state) {
                case RAW:
                    stateStr = "raw";
                    break;
                case CHOPPED:
                    stateStr = "chopped";
                    break;
                case COOKED:
                    stateStr = "cooked";
                    break;
                case BURNED:
                    stateStr = "burned";
                    break;
            }

            String path = "/items/ingredients/" + nameLower + "/" + nameLower + "_" + stateStr + ".png";
            this.image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Failed to load image for ingredient: " + name + " in state: " + state);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading image for ingredient: " + name);
            e.printStackTrace();
        }
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
        loadImage(); // Reload image when state changes
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
            loadImage();
        }
    }

    @Override
    public void cook() {
        if (canBeCooked()) {
            this.state = IngredientState.COOKED;
            loadImage();
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
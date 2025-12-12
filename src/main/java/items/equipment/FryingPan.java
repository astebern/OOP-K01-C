package items.equipment;

import items.Preparable;
import items.food.Ingredient;
import items.food.IngredientState;
import javax.imageio.ImageIO;
import java.io.IOException;
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

        loadImage();
    }

    private void loadImage() {
        try {
            String path = "/items/kitchenUtensils/frying_pan.png";
            this.image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Failed to load image for FryingPan");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading image for FryingPan");
            e.printStackTrace();
        }
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

    @Override
    protected String getCookingImagePath(Ingredient ingredient) {
        String name = ingredient.getName().toLowerCase();
        IngredientState state = ingredient.getState();

        // If burned, show the burned ingredient image
        if (state == IngredientState.BURNED) {
            return "/items/ingredients/" + name + "/" + name + "_burned.png";
        }

        // Otherwise show cooking image
        switch (name) {
            case "daging":
                return "/items/cooking/fryMeat.png";
            case "ikan":
                return "/items/cooking/fryMeat.png"; // Use same image for both meat and fish
            case "udang":
                return "/items/cooking/fryShrimp.png";
            case "tomat":
                return "/items/cooking/fryTomato.png";
            default:
                return null;
        }
    }
}
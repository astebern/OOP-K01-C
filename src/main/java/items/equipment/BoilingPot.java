package items.equipment;

import items.Preparable;
import items.food.Ingredient;
import items.food.IngredientState;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoilingPot extends KitchenUtensil implements CookingDevice {
    private int capacity;
    private List<String> acceptableNames;
    private boolean isCooking; 

    public BoilingPot() {
        super(); 
        this.capacity = 1; 
        this.isCooking = false;

        // Ingredients that can be boiled
        this.acceptableNames = new ArrayList<>();
        acceptableNames.add("Pasta");

        loadImage();
    }

    private void loadImage() {
        try {
            String path = "/items/kitchenUtensils/boiling_pot.png";
            this.image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Failed to load image for BoilingPot");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading image for BoilingPot");
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPortable() {
        return false; // BoilingPot cannot be picked up
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
            contents.add(item); // Actually add the item to the pot!
            System.out.println("Added " + ((Ingredient)item).getName() + " to BoilingPot");
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

    @Override
    protected String getCookingImagePath(Ingredient ingredient) {
        String name = ingredient.getName().toLowerCase();
        IngredientState state = ingredient.getState();

        // If burned, show the burned ingredient image
        if (state == IngredientState.BURNED) {
            return "/items/ingredients/" + name + "/" + name + "_burned.png";
        }

        // Otherwise show cooking image
        if (name.equals("pasta")) {
            return "/items/cooking/boilPasta.png";
        }

        return null; // No specific cooking image
    }
}
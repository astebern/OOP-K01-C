package items.equipment;

import items.Item;
import items.Preparable;
import items.food.Ingredient;
import items.food.IngredientState;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class KitchenUtensil extends Item {
    protected List<Preparable> contents;
    protected BufferedImage cookingImage; // Image showing what's being cooked

    public KitchenUtensil() {
        this.contents = new ArrayList<>();
    }

    public List<Preparable> getContents() {
        return contents;
    }

    public void addContent(Preparable item) {
        contents.add(item);
        updateCookingImage();
    }
    
    public void clearContents() {
        contents.clear();
        cookingImage = null;
    }

    /**
     * Updates the cooking image based on the current contents.
     * Should be called when contents change or ingredient state changes.
     */
    public void updateCookingImage() {
        if (contents.isEmpty()) {
            cookingImage = null;
            return;
        }

        Preparable item = contents.get(0);
        if (!(item instanceof Ingredient)) {
            cookingImage = null;
            return;
        }

        Ingredient ingredient = (Ingredient) item;

        // Load cooking image immediately when ingredient is added
        String imagePath = getCookingImagePath(ingredient);
        if (imagePath != null) {
            try {
                cookingImage = ImageIO.read(getClass().getResourceAsStream(imagePath));
            } catch (IOException e) {
                System.err.println("Failed to load cooking image: " + imagePath);
                e.printStackTrace();
                cookingImage = null;
            } catch (Exception e) {
                System.err.println("Error loading cooking image: " + imagePath);
                e.printStackTrace();
                cookingImage = null;
            }
        } else {
            cookingImage = null;
        }
    }

    /**
     * Gets the path to the cooking image for this utensil and ingredient.
     * Override in subclasses to provide specific cooking images.
     */
    protected abstract String getCookingImagePath(Ingredient ingredient);

    /**
     * Returns the cooking image if available, otherwise returns the utensil's default image.
     */
    @Override
    public BufferedImage getImage() {
        if (cookingImage != null) {
            return cookingImage;
        }
        return super.getImage();
    }
}
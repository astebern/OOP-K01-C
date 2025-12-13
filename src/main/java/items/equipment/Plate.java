package items.equipment;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Plate extends KitchenUtensil {
    private boolean isDirty;

    public Plate() {
        super();
        this.isDirty = false;
        loadImage();
    }

    /**
     * Factory method to create a clean plate
     */
    public static Plate createClean() {
        return new Plate();
    }

    private void loadImage() {
        try {
            String path;
            if (isDirty) {
                path = "/items/kitchenUtensils/dirtyPlate.png";
            } else {
                path = "/items/kitchenUtensils/plate.png";
            }
            this.image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Failed to load image for Plate (dirty=" + isDirty + ")");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading image for Plate (dirty=" + isDirty + ")");
            e.printStackTrace();
        }
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean dirty) {
        this.isDirty = dirty;
        loadImage(); // Reload image when dirty state changes
    }

    public void setIsDirty() {
        this.contents.clear();
        this.isDirty = true;
    }

    public void clean() {
        this.isDirty = false;
        this.contents.clear();
        loadImage(); // Reload image to show clean plate
    }

    @Override
    public void addContent(items.Preparable item) {
        // Plates can hold multiple ingredients (no capacity limit)
        super.addContent(item);
    }

    @Override
    protected String getCookingImagePath(items.food.Ingredient ingredient) {
        return null;
    }

    @Override
    public String toString() {
        String status = isDirty ? "Dirty" : "Clean";
        return "Plate [" + status + "] - Contents: " + contents.size();
    }
}
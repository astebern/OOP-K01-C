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

    private void loadImage() {
        try {
            String path = "/items/kitchenUtensils/plate.png";
            this.image = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            System.err.println("Failed to load image for Plate");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading image for Plate");
            e.printStackTrace();
        }
    }

    public boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public void setIsDirty() {
        this.contents.clear();
        this.isDirty = true;
    }
    
    @Override
    public String toString() {
        String status = isDirty ? "Dirty" : "Clean";
        return "Plate [" + status + "] - Contents: " + contents.size();
    }
}
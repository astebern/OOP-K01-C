package items.food;

import items.Item;
import items.Preparable;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Dish extends Item implements Preparable {
    private String name;
    private List<Preparable> components;

    public Dish(String name, List<Preparable> components) {
        this.name = name;
        this.components = new ArrayList<>(components);
        loadDishImage();
    }

    private void loadDishImage() {
        // Only load images for actual dishes, not signal dishes
        if (name.startsWith("__")) {
            return; // Skip loading for signal dishes like __DISH_PLACED__
        }

        try {
            String imagePath = getDishImagePath();
            if (imagePath != null) {
                this.image = ImageIO.read(getClass().getResourceAsStream(imagePath));
                System.out.println("Dish: Loaded image for " + name + " from " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Failed to load image for dish: " + name);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading image for dish: " + name);
            e.printStackTrace();
        }
    }

    private String getDishImagePath() {
        // Map dish names to image files
        switch (name) {
            case "Pasta Marinara":
                return "/items/serving/marinara.png";
            case "Pasta Bolognese":
                return "/items/serving/bolognese.png";
            case "Pasta Ai Gamberetti":
                return "/items/serving/pasta_shrimp.png";
            case "Pasta Di Pesce":
                return "/items/serving/pasta_fish.png";
            case "Pasta Frutti di Mare":
                return "/items/serving/frutti.png";
            default:
                System.err.println("No image mapping found for dish: " + name);
                return null;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Preparable> getComponents() {
        return components;
    }

    public void setComponents(List<Preparable> components) {
        this.components = components;
    }

    public void addComponent(Preparable component) {
        this.components.add(component);
    }

    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (Preparable p : components) {
            if (p instanceof Ingredient) {
                ingredients.add((Ingredient) p);
            }
        }
        return ingredients;
    }

    @Override
    public String toString() {
        return name + " (" + components.size() + " items)";
    }

    // Preparable interface implementation
    @Override
    public boolean canBeChopped() {
        return false; // Dishes cannot be chopped
    }

    @Override
    public boolean canBeCooked() {
        return false; // Dishes cannot be cooked
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return true; // Dishes can be placed on plates
    }

    @Override
    public void cook() {
        // Dishes cannot be cooked - do nothing
    }

    @Override
    public void chop() {
        // Dishes cannot be chopped - do nothing
    }

    @Override
    public void setChoppable(boolean choppable) {
        // Dishes cannot be chopped - do nothing
    }

    @Override
    public void setCookable(boolean cookable) {
        // Dishes cannot be cooked - do nothing
    }

    @Override
    public void setCanBePlacedOnPlate(boolean canPlace) {
        // Always true for dishes - do nothing
    }
}
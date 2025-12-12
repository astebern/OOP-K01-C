package stations;

import items.Item;
import items.Preparable;
import items.food.Dish;
import items.food.Ingredient;
import items.food.IngredientState;
import items.food.Recipe;
import items.food.Requirement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssemblyStation extends Station {
    private static List<Recipe> recipes;
    private List<Ingredient> ingredients; // Ingredients currently on this station

    public AssemblyStation() {
        this.ingredients = new ArrayList<>();
    }

    static {
        // Initialize recipes
        recipes = new ArrayList<>();

        // Pasta Marinara: Pasta (Cooked) + Tomat (Cooked)
        recipes.add(new Recipe("Pasta Marinara", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Tomat", IngredientState.COOKED)
        )));

        // Pasta Bolognese: Pasta (Cooked) + Daging (Cooked)
        recipes.add(new Recipe("Pasta Bolognese", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Daging", IngredientState.COOKED)
        )));

        // Pasta with Fish: Pasta (Cooked) + Ikan (Cooked)
        recipes.add(new Recipe("Pasta with Fish", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Ikan", IngredientState.COOKED)
        )));

        // Pasta with Shrimp: Pasta (Cooked) + Udang (Cooked)
        recipes.add(new Recipe("Pasta with Shrimp", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Udang", IngredientState.COOKED)
        )));

        // Pasta Frutti di Mare: Pasta (Cooked) + Udang (Cooked) + Ikan (Cooked)
        recipes.add(new Recipe("Pasta Frutti di Mare", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Udang", IngredientState.COOKED),
            new Requirement("Ikan", IngredientState.COOKED)
        )));
    }

    /**
     * Adds an ingredient to this station and checks if a dish can be assembled
     * @param ingredient The ingredient to add
     * @param gameMap The game map
     * @param x X coordinate of this station
     * @param y Y coordinate of this station
     * @return The assembled dish if a recipe matches, null otherwise
     */
    public Dish addIngredient(Ingredient ingredient, map.GameMap gameMap, int x, int y) {
        ingredients.add(ingredient);
        System.out.println("AssemblyStation: Added " + ingredient.getName() + " (" + ingredients.size() + " ingredients total)");

        // Check if current ingredients match any recipe
        return tryAssemble(gameMap, x, y);
    }

    /**
     * Tries to assemble a dish from current ingredients
     * @param gameMap The game map to update the tile
     * @param x X coordinate of this station
     * @param y Y coordinate of this station
     * @return The assembled dish if a recipe matches, null otherwise
     */
    private Dish tryAssemble(map.GameMap gameMap, int x, int y) {
        if (ingredients.isEmpty()) {
            return null;
        }

        // Check each recipe to see if ingredients match
        for (Recipe recipe : recipes) {
            if (matchesRecipe(ingredients, recipe)) {
                // Create the dish
                List<Preparable> components = new ArrayList<>(ingredients);
                Dish dish = new Dish(recipe.getName(), components);

                // Load the dish image
                loadDishImage(dish, recipe.getName());

                // Clear ingredients from station
                ingredients.clear();

                // Place the dish on the tile
                gameMap.placeItemAt(x, y, dish);

                System.out.println("AssemblyStation: Created " + recipe.getName() + "!");
                return dish;
            }
        }

        return null;
    }

    /**
     * Gets the ingredients currently on this station
     */
    public List<Ingredient> getIngredients() {
        return new ArrayList<>(ingredients);
    }

    /**
     * Clears all ingredients from this station
     */
    public void clearIngredients() {
        ingredients.clear();
    }

    /**
     * Checks if the ingredients match a recipe
     */
    private boolean matchesRecipe(List<Ingredient> ingredients, Recipe recipe) {
        List<Requirement> requirements = recipe.getRequiredComponents();

        // Check if we have the right number of ingredients
        if (ingredients.size() != requirements.size()) {
            return false;
        }

        // Check each requirement
        for (Requirement req : requirements) {
            boolean found = false;
            for (Ingredient ing : ingredients) {
                if (ing.getName().equalsIgnoreCase(req.getIngredientName()) &&
                    ing.getState() == req.getRequiredState()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }

        return true;
    }

    /**
     * Loads the appropriate image for a dish based on its name
     */
    private void loadDishImage(Dish dish, String dishName) {
        String imagePath = null;

        if (dishName.equalsIgnoreCase("Pasta Marinara")) {
            imagePath = "/items/serving/marinara.png";
        } else if (dishName.equalsIgnoreCase("Pasta Bolognese")) {
            imagePath = "/items/serving/bolognese.png";
        } else if (dishName.equalsIgnoreCase("Pasta with Fish")) {
            imagePath = "/items/serving/pasta_fish.png";
        } else if (dishName.equalsIgnoreCase("Pasta with Shrimp")) {
            imagePath = "/items/serving/pasta_shrimp.png";
        } else if (dishName.equalsIgnoreCase("Pasta Frutti di Mare")) {
            imagePath = "/items/serving/frutti.png";
        }

        if (imagePath != null) {
            try {
                BufferedImage dishImage = ImageIO.read(getClass().getResourceAsStream(imagePath));
                dish.setImage(dishImage);
            } catch (IOException e) {
                System.err.println("Failed to load dish image: " + imagePath);
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error loading dish image: " + imagePath);
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets all available recipes
     */
    public static List<Recipe> getRecipes() {
        return recipes;
    }
}


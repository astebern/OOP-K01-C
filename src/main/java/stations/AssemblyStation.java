package stations;

import items.Item;
import items.Preparable;
import items.equipment.Plate;
import items.food.Dish;
import items.food.Ingredient;
import items.food.IngredientState;
import items.food.Recipe;
import items.food.Requirement;
import map.GameMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssemblyStation extends Station {
    private Item currentItem; // Can hold only 1 item (ingredient, dish, or plate)
    private Plate plateOnStation; // Track plate separately for special handling
    private static final List<Recipe> recipes;

    static {
        // Initialize available recipes
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

        // Pasta Ai Gamberetti: Pasta (Cooked) + Udang (Cooked)
        recipes.add(new Recipe("Pasta Ai Gamberetti", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Udang", IngredientState.COOKED)
        )));

        // Pasta Di Pesce: Pasta (Cooked) + Ikan (Cooked)
        recipes.add(new Recipe("Pasta Di Pesce", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Ikan", IngredientState.COOKED)
        )));

        // Pasta Frutti di Mare: Can be made two ways
        // Way 1: Pasta Ai Gamberetti + Ikan = Pasta + Udang + Ikan
        // Way 2: Pasta Di Pesce + Udang = Pasta + Ikan + Udang
        // Both expand to the same 3 ingredients, so one recipe handles both
        recipes.add(new Recipe("Pasta Frutti di Mare", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Udang", IngredientState.COOKED),
            new Requirement("Ikan", IngredientState.COOKED)
        )));
    }

    public AssemblyStation() {
        this.currentItem = null;
        this.plateOnStation = null;
    }

    public static List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Adds a plate to the assembly station
     * Returns true if successful, false if station already has an item
     */
    public boolean addPlate(Plate plate) {
        if (currentItem == null) {
            this.plateOnStation = plate;
            this.currentItem = plate;
            System.out.println("AssemblyStation: Plate placed on station");
            return true;
        } else {
            System.out.println("AssemblyStation: Cannot add plate - station already has an item");
            return false;
        }
    }

    /**
     * Removes and returns the plate from the station
     */
    public Plate removePlate() {
        if (plateOnStation != null) {
            Plate plate = this.plateOnStation;
            this.plateOnStation = null;
            this.currentItem = null;
            System.out.println("AssemblyStation: Plate removed from station");
            return plate;
        }
        return null;
    }

    /**
     * Checks if there's a plate on the station
     */
    public boolean hasPlate() {
        return plateOnStation != null;
    }

    /**
     * Gets the plate on the station (without removing it)
     */
    public Plate getPlate() {
        return plateOnStation;
    }

    /**
     * Gets the list of loose ingredients on the station (not on a plate)
     */
    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        if (currentItem instanceof Dish) {
            Dish dish = (Dish) currentItem;
            ingredients.addAll(dish.getIngredients());
        } else if (currentItem instanceof Ingredient && plateOnStation == null) {
            ingredients.add((Ingredient) currentItem);
        }
        return ingredients;
    }

    /**
     * Adds an ingredient to the assembly station.
     * Returns a Dish if a recipe is completed, null otherwise.
     * The ingredient stays on the station (either on plate or combined with other items).
     */
    public Dish addIngredient(Ingredient ingredient, GameMap gameMap, int stationX, int stationY) {
        // Case 1: There's a plate on the station - add ingredient to plate
        if (plateOnStation != null) {
            if (!plateOnStation.getContents().isEmpty()) {
                System.out.println("AssemblyStation: Plate already has an item on it");
                return null; // Reject - plate is full
            }

            plateOnStation.addContent(ingredient);
            System.out.println("AssemblyStation: Ingredient added to plate (stays on station)");

            // Return empty dish to signal success - ingredient stays on plate on station
            return new Dish("__INGREDIENT_ON_PLATE__", new ArrayList<>());
        }

        // Case 2: No plate - check if we can combine with existing item
        if (currentItem == null) {
            // Station is empty - place ingredient on station
            this.currentItem = ingredient;
            System.out.println("AssemblyStation: Ingredient placed on station");
            return new Dish("__DISH_PLACED__", new ArrayList<>()); // Signal ingredient was placed
        }

        // Case 3: Try to combine with existing item(s) on station
        List<Preparable> components = new ArrayList<>();

        // Collect existing components
        if (currentItem instanceof Dish) {
            Dish existingDish = (Dish) currentItem;
            components.addAll(existingDish.getComponents());
        } else if (currentItem instanceof Ingredient) {
            components.add((Preparable) currentItem);
        }

        // Add new ingredient
        components.add(ingredient);

        // Try to match with a recipe
        Dish assembledDish = tryAssembleDish(components);

        if (assembledDish != null) {
            // Recipe matched! Clear station and return the completed dish
            this.currentItem = null;
            System.out.println("AssemblyStation: Recipe matched - " + assembledDish.getName());
            return assembledDish;
        } else {
            // No recipe match - reject the ingredient (don't add it to station)
            System.out.println("AssemblyStation: Ingredient rejected - no recipe matches these ingredients");
            return null; // Ingredient stays in chef's inventory
        }
    }

    /**
     * Adds a dish to the assembly station (for combining dishes or placing on plate).
     */
    public Dish addDish(Dish dish, GameMap gameMap, int stationX, int stationY) {
        // Case 1: There's a plate on the station - add dish to plate
        if (plateOnStation != null) {
            if (!plateOnStation.getContents().isEmpty()) {
                System.out.println("AssemblyStation: Plate already has an item on it");
                return null; // Reject - plate is full
            }

            // Add the dish itself to the plate (not individual components)
            plateOnStation.addContent(dish);
            System.out.println("AssemblyStation: Dish added to plate (stays on station)");

            // Return empty dish to signal success - dish stays on plate on station
            return new Dish("__DISH_ON_PLATE__", new ArrayList<>());
        }

        // Case 2: No plate - check if we can combine with existing item
        if (currentItem == null) {
            // Station is empty - place dish on station
            this.currentItem = dish;
            System.out.println("AssemblyStation: Dish placed on station");
            return new Dish("__DISH_PLACED__", new ArrayList<>()); // Signal that dish was placed
        }

        // Case 3: Try to combine with existing item(s) on station
        List<Preparable> components = new ArrayList<>();

        // Collect existing components
        if (currentItem instanceof Dish) {
            Dish existingDish = (Dish) currentItem;
            components.addAll(existingDish.getComponents());
        } else if (currentItem instanceof Ingredient) {
            components.add((Preparable) currentItem);
        }

        // Add new dish components
        components.addAll(dish.getComponents());

        // Try to match with a recipe
        Dish assembledDish = tryAssembleDish(components);

        if (assembledDish != null) {
            // Recipe matched! Clear station and return the completed dish
            this.currentItem = null;
            System.out.println("AssemblyStation: Recipe matched - " + assembledDish.getName());
            return assembledDish;
        } else {
            // No recipe match - reject the dish (don't add it to station)
            System.out.println("AssemblyStation: Dish rejected - no recipe matches these ingredients");
            return null; // Dish stays in chef's inventory
        }
    }

    /**
     * Tries to assemble a dish from the given components by matching against recipes.
     * Returns the assembled Dish if a recipe matches, null otherwise.
     * Expands any Dish components to their base ingredients for matching.
     */
    private Dish tryAssembleDish(List<Preparable> components) {
        // Expand all components - if a component is a Dish, get its ingredients
        List<Preparable> expandedComponents = new ArrayList<>();
        for (Preparable component : components) {
            if (component instanceof Dish) {
                // Expand dish to its base ingredients
                Dish dish = (Dish) component;
                expandedComponents.addAll(dish.getComponents());
            } else {
                expandedComponents.add(component);
            }
        }

        // Try to match with recipes using expanded components
        for (Recipe recipe : recipes) {
            if (matchesRecipe(expandedComponents, recipe)) {
                // Create new dish with expanded components
                return new Dish(recipe.getName(), expandedComponents);
            }
        }
        return null;
    }

    /**
     * Checks if the given components match a recipe's requirements.
     */
    private boolean matchesRecipe(List<Preparable> components, Recipe recipe) {
        List<Requirement> requirements = recipe.getRequiredComponents();

        // Must have exact number of components
        if (components.size() != requirements.size()) {
            return false;
        }

        // Check each requirement is satisfied
        List<Preparable> remainingComponents = new ArrayList<>(components);

        for (Requirement requirement : requirements) {
            boolean found = false;

            for (int i = 0; i < remainingComponents.size(); i++) {
                Preparable component = remainingComponents.get(i);

                if (component instanceof Ingredient) {
                    Ingredient ingredient = (Ingredient) component;

                    if (ingredient.getName().equals(requirement.getIngredientName()) &&
                        ingredient.getState() == requirement.getRequiredState()) {
                        // Found a match - remove it and continue
                        remainingComponents.remove(i);
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                return false; // Requirement not satisfied
            }
        }

        return remainingComponents.isEmpty(); // All components used
    }

    /**
     * Gets the current item on the station (can be ingredient, dish, or plate)
     */
    public Item getCurrentItem() {
        return currentItem;
    }

    /**
     * Removes and returns the current item from the station
     */
    public Item removeCurrentItem() {
        Item item = this.currentItem;
        this.currentItem = null;
        this.plateOnStation = null;
        System.out.println("AssemblyStation: Item removed from station");
        return item;
    }

    /**
     * Checks if the station has any item on it
     */
    public boolean hasItem() {
        return currentItem != null;
    }

    /**
     * Clears the station completely
     */
    public void clear() {
        this.currentItem = null;
        this.plateOnStation = null;
        System.out.println("AssemblyStation: Station cleared");
    }
}


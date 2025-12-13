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
    private static final List<Recipe> intermediateRecipes; // Recipes that can't be ordered

static {
        // --- RESEP UTAMA (Bisa Dipesan) ---
        recipes = new ArrayList<>();
        
        recipes.add(new Recipe("Pasta Marinara", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Tomat", IngredientState.COOKED)
        )));

        recipes.add(new Recipe("Pasta Bolognese", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Daging", IngredientState.COOKED)
        )));

        // Note: Frutti di Mare is NOT in main recipes anymore!
        // It's created by combining intermediate dishes

        // --- RESEP PERANTARA (TIDAK Bisa Dipesan) ---
        intermediateRecipes = new ArrayList<>();

        // Pasta + Ikan = Pasta Ikan (intermediate, can't be ordered)
        intermediateRecipes.add(new Recipe("Pasta Di Pesce", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Ikan", IngredientState.COOKED)
        )));

        // Pasta + Udang = Pasta Udang (intermediate, can't be ordered)
        intermediateRecipes.add(new Recipe("Pasta Ai Gamberetti", Arrays.asList(
            new Requirement("Pasta", IngredientState.COOKED),
            new Requirement("Udang", IngredientState.COOKED)
        )));

        // Frutti di Mare = Pasta Ikan + Udang OR Pasta Udang + Ikan
        // This will be handled specially in addIngredient() and addDish()
        intermediateRecipes.add(new Recipe("Pasta Frutti di Mare", Arrays.asList(
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
        // Only return main recipes that can be ordered (Marinara, Bolognese)
        // Intermediate recipes (Pasta Ikan, Pasta Udang, Frutti di Mare) cannot be ordered
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
/**
     * Adds an ingredient to the assembly station.
     * Modified: Allows stacking multiple ingredients on a plate to form a recipe.
     */
/**
     * Adds an ingredient to the assembly station.
     * Handles stacking ingredients on a plate, checking for recipes (main & intermediate),
     * and merging items on the station surface.
     */
    public Dish addIngredient(Ingredient ingredient, GameMap gameMap, int stationX, int stationY) {
        // KASUS 1: Ada piring di Station
        if (plateOnStation != null) {
            // 1. Ambil isi piring saat ini & expand jika berupa dish
            List<Preparable> potentialComponents = new ArrayList<>();

            System.out.println("=== DEBUG: Adding " + ingredient.getName() + " (state: " + ingredient.getState() + ") to plate ===");
            System.out.println("Plate currently has " + plateOnStation.getContents().size() + " items");

            // Expand contents: jika ada dish, ambil ingredients-nya
            for (Preparable content : plateOnStation.getContents()) {
                if (content instanceof Dish) {
                    Dish existingDish = (Dish) content;
                    System.out.println("  - Found Dish: " + existingDish.getName() + " with " + existingDish.getComponents().size() + " components");
                    for (Preparable comp : existingDish.getComponents()) {
                        if (comp instanceof Ingredient) {
                            Ingredient ing = (Ingredient) comp;
                            System.out.println("    * " + ing.getName() + " (state: " + ing.getState() + ")");
                        }
                    }
                    potentialComponents.addAll(existingDish.getComponents());
                } else if (content instanceof Ingredient) {
                    Ingredient ing = (Ingredient) content;
                    System.out.println("  - Found Ingredient: " + ing.getName() + " (state: " + ing.getState() + ")");
                    potentialComponents.add(content);
                }
            }

            // Tambahkan ingredient baru
            potentialComponents.add(ingredient);

            System.out.println("Total components after adding: " + potentialComponents.size());
            for (Preparable comp : potentialComponents) {
                if (comp instanceof Ingredient) {
                    Ingredient ing = (Ingredient) comp;
                    System.out.println("  > " + ing.getName() + " (state: " + ing.getState() + ")");
                }
            }

            // 2. CEK INTERMEDIATE RECIPES DULU (Pasta Ikan, Pasta Udang, atau Frutti di Mare)
            Dish intermediateDish = tryAssembleDish(potentialComponents, intermediateRecipes);

            if (intermediateDish != null) {
                // Berhasil jadi intermediate dish ATAU Frutti di Mare
                plateOnStation.clearContents();
                plateOnStation.addContent(intermediateDish);
                System.out.println("AssemblyStation: Dish created - " + intermediateDish.getName());

                // Return sinyal agar inventory Chef kosong (item pindah ke piring)
                return new Dish("__INGREDIENT_ON_PLATE__", new ArrayList<>());
            }

            // 3. CEK RESEP UTAMA (Marinara, Bolognese) - hanya jika bukan Frutti
            Dish assembledDish = tryAssembleDish(potentialComponents, recipes);

            if (assembledDish != null) {
                // Berhasil jadi masakan utama (Marinara atau Bolognese)
                plateOnStation.clearContents();
                plateOnStation.addContent(assembledDish);
                System.out.println("AssemblyStation: Recipe completed - " + assembledDish.getName());
                
                // Return sinyal agar inventory Chef kosong (item pindah ke piring)
                return new Dish("__INGREDIENT_ON_PLATE__", new ArrayList<>()); 
            }

            // 4. Jika tidak cocok resep, tumpuk saja sebagai bahan terpisah
            plateOnStation.addContent(ingredient);
            System.out.println("AssemblyStation: Added " + ingredient.getName() + " to plate (waiting for more ingredients)");

            // Return sinyal agar inventory Chef kosong
            return new Dish("__INGREDIENT_ON_PLATE__", new ArrayList<>());
        }

        // KASUS 2: Tidak ada piring - Taruh di meja (Station kosong)
        if (currentItem == null) {
            this.currentItem = ingredient;
            System.out.println("AssemblyStation: Ingredient placed on station");
            return new Dish("__DISH_PLACED__", new ArrayList<>()); // Sinyal item ditaruh di meja
        }

        // KASUS 3: Tidak ada piring - Gabungkan dengan item yang sudah ada di meja
        List<Preparable> components = new ArrayList<>();
        if (currentItem instanceof Dish) {
            Dish existingDish = (Dish) currentItem;
            components.addAll(existingDish.getComponents());
        } else if (currentItem instanceof Ingredient) {
            components.add((Preparable) currentItem);
        }
        components.add(ingredient);

        // Check intermediate recipes first
        Dish intermediateDish = tryAssembleDish(components, intermediateRecipes);
        if (intermediateDish != null) {
            this.currentItem = null;
            System.out.println("AssemblyStation: Dish created - " + intermediateDish.getName());
            return intermediateDish;
        }

        // Then check main recipes
        Dish assembledDish = tryAssembleDish(components, recipes);

        if (assembledDish != null) {
            this.currentItem = null; // Item diambil dari station (jadi Dish di tangan Chef)
            System.out.println("AssemblyStation: Recipe matched - " + assembledDish.getName());
            return assembledDish; // Return Dish asli untuk dipegang Chef
        } else {
            System.out.println("AssemblyStation: Ingredient rejected - no recipe matches");
            return null; // Gagal, item tetap di tangan Chef
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


    private Dish tryAssembleDish(List<Preparable> components, List<Recipe> recipesToCheck) {
        System.out.println("=== tryAssembleDish: Checking " + recipesToCheck.size() + " recipes ===");

        // 1. Expand components
        // Jika ada komponen yang berupa Dish (misal: "Pasta Di Pesce"),
        // kita perlu memecahnya kembali menjadi bahan dasarnya (Pasta + Ikan)
        // agar bisa dicocokkan dengan resep utuh (misal: Frutti di Mare = Pasta + Ikan + Udang).
        List<Preparable> expandedComponents = new ArrayList<>();
        
        System.out.println("Expanding " + components.size() + " components...");
        for (Preparable component : components) {
            if (component instanceof Dish) {
                // Jika komponen adalah Dish, ambil semua isi (ingredients)-nya
                Dish dish = (Dish) component;
                System.out.println("  - Expanding Dish: " + dish.getName() + " into " + dish.getComponents().size() + " components");
                expandedComponents.addAll(dish.getComponents());
            } else if (component instanceof Ingredient) {
                // Jika komponen adalah Ingredient biasa, langsung tambahkan
                Ingredient ing = (Ingredient) component;
                System.out.println("  - Adding Ingredient: " + ing.getName() + " (state: " + ing.getState() + ")");
                expandedComponents.add(component);
            }
        }

        System.out.println("After expansion: " + expandedComponents.size() + " components");
        for (Preparable comp : expandedComponents) {
            if (comp instanceof Ingredient) {
                Ingredient ing = (Ingredient) comp;
                System.out.println("  >> " + ing.getName() + " (state: " + ing.getState() + ")");
            }
        }

        // 2. Cek kecocokan dengan daftar resep yang diberikan
        for (Recipe recipe : recipesToCheck) {
            System.out.println("Checking recipe: " + recipe.getName() + " (requires " + recipe.getRequiredComponents().size() + " items)");
            if (matchesRecipe(expandedComponents, recipe)) {
                System.out.println("  ✓ MATCH! Creating dish: " + recipe.getName());
                // Jika cocok, buat Dish baru dengan nama resep tersebut
                return new Dish(recipe.getName(), expandedComponents);
            } else {
                System.out.println("  ✗ No match");
            }
        }
        
        System.out.println("No recipe matched.");
        return null; // Tidak ada resep yang cocok
    }

    /**
     * Overload method lama untuk kompatibilitas.
     * Secara default akan mengecek daftar resep utama ('recipes').
     * Dipanggil jika kita tidak menentukan list resep secara spesifik.
     */
    private Dish tryAssembleDish(List<Preparable> components) {
        return tryAssembleDish(components, recipes);
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


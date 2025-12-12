package stations;

import items.Item;
import items.Preparable;
import items.food.Ingredient;
import items.food.IngredientState;

public class CookingStation extends Station {
    
    public enum CookingEquipment {
        BOILING_POT,
        FRYING_PAN
    }

    private CookingEquipment equipment;
    private static final long COOKING_DURATION = 12000; // 12 seconds to become COOKED
    private static final long BURNING_DURATION = 24000; // 24 seconds total to become BURNED

    private Thread cookingThread;
    private volatile boolean shouldStop = false;
    private Item itemBeingCooked;

    public CookingStation() {
        this.equipment = null; // Will be determined by utensil placed on the station
    }

    public CookingStation(CookingEquipment equipment) {
        this.equipment = equipment;
    }

    public CookingEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(CookingEquipment equipment) {
        this.equipment = equipment;
    }

    /**
     * Starts the cooking process for items inside a cooking utensil (BoilingPot/FryingPan).
     * The thread runs continuously until the item is picked up from the utensil.
     * States: COOKING (0-12s) → COOKED (12-24s) → BURNED (24s+)
     */
    public boolean startCooking(Item utensilOnTile) {
        if (utensilOnTile == null) {
            System.out.println("CookingStation: No cooking utensil on station");
            return false;
        }

        // Check if it's a cooking utensil
        if (!(utensilOnTile instanceof items.equipment.CookingDevice)) {
            System.out.println("CookingStation: Item on station is not a cooking utensil");
            return false;
        }

        items.equipment.KitchenUtensil utensil = (items.equipment.KitchenUtensil) utensilOnTile;

        // Check if utensil has contents
        if (utensil.getContents().isEmpty()) {
            System.out.println("CookingStation: Cooking utensil is empty - add ingredient first");
            return false;
        }

        // Get the ingredient inside the utensil
        Preparable preparable = utensil.getContents().get(0);
        if (!(preparable instanceof Ingredient)) {
            System.out.println("CookingStation: Content is not an ingredient");
            return false;
        }

        Ingredient ingredient = (Ingredient) preparable;

        if (!preparable.canBeCooked()) {
            System.out.println("CookingStation: " + ingredient.getName() +
                             " cannot be cooked (current state: " + ingredient.getState() + ")");
            return false;
        }

        // If already cooking, don't start a new thread
        if (isInProgress && cookingThread != null && cookingThread.isAlive()) {
            System.out.println("CookingStation: Already cooking " + ingredient.getName());
            return false;
        }

        // Start cooking process
        System.out.println("CookingStation: Starting to cook " + ingredient.getName() +
                         " in " + utensilOnTile.getClass().getSimpleName());
        isInProgress = true;
        showProgressBar = true;
        startTime = System.currentTimeMillis();
        pausedTime = 0;
        lastPauseTime = 0;
        progressPercent = 0.0f;
        shouldStop = false;
        itemBeingCooked = utensilOnTile; // Store the utensil reference

        cookingThread = new Thread(() -> {
            try {
                // Set initial state to COOKING
                ingredient.setState(IngredientState.COOKING);
                utensil.updateCookingImage(); // Update to show cooking image
                System.out.println("CookingStation: " + ingredient.getName() + " is now COOKING");

                while (!shouldStop && isInProgress) {
                    long elapsed = System.currentTimeMillis() - startTime;

                    // Update progress percentage (0-100% over 24 seconds)
                    progressPercent = Math.min(100.0f, (elapsed / (float) BURNING_DURATION) * 100.0f);

                    // State transitions based on time
                    if (elapsed >= BURNING_DURATION) {
                        // After 24 seconds: BURNED
                        if (ingredient.getState() != IngredientState.BURNED) {
                            ingredient.setState(IngredientState.BURNED);
                            utensil.updateCookingImage(); // Update image for burned state
                            System.out.println("CookingStation: " + ingredient.getName() +
                                             " is now BURNED! (overcooked)");
                        }
                        // Continue cooking (stay burned)
                        progressPercent = 100.0f;

                    } else if (elapsed >= COOKING_DURATION) {
                        // After 12 seconds: COOKED
                        if (ingredient.getState() != IngredientState.COOKED) {
                            ingredient.setState(IngredientState.COOKED);
                            utensil.updateCookingImage(); // Update image for cooked state
                            System.out.println("CookingStation: " + ingredient.getName() +
                                             " is now COOKED!");
                        }
                    }
                    // 0-12 seconds: remains COOKING (already set)

                    Thread.sleep(100); // Update every 100ms
                }

                System.out.println("CookingStation: Cooking thread stopped for " + ingredient.getName());

            } catch (InterruptedException e) {
                System.out.println("CookingStation: Cooking interrupted");
                Thread.currentThread().interrupt();
            }
        });

        cookingThread.start();
        return true;
    }

    /**
     * Stops the cooking thread (called when item is picked up).
     */
    public void stopCooking() {
        shouldStop = true;
        if (cookingThread != null && cookingThread.isAlive()) {
            cookingThread.interrupt();
        }
        isInProgress = false;
        showProgressBar = false;
        progressPercent = 0.0f;
        startTime = 0;
        itemBeingCooked = null;
        System.out.println("CookingStation: Cooking stopped");
    }

    /**
     * Checks if there's a cooking utensil with a cookable ingredient inside.
     */
    public boolean hasCookableIngredient(Item item) {
        if (item instanceof items.equipment.CookingDevice && item instanceof items.equipment.KitchenUtensil) {
            items.equipment.KitchenUtensil utensil = (items.equipment.KitchenUtensil) item;
            if (!utensil.getContents().isEmpty()) {
                Preparable content = utensil.getContents().get(0);
                if (content instanceof Ingredient) {
                    return content.canBeCooked();
                }
            }
        }
        return false;
    }

    public Item getItemBeingCooked() {
        return itemBeingCooked;
    }

    @Override
    public String toString() {
        return "CookingStation(" + equipment + ")";
    }
}


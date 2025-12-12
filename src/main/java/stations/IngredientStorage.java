package stations;

import items.food.Ingredient;

public class IngredientStorage extends Station {
    private String ingredientType; // Type of ingredient this storage provides (e.g., "Daging", "Pasta")

    /**
     * Sets the type of ingredient this storage provides
     */
    public void setIngredientType(String ingredientType) {
        this.ingredientType = ingredientType;
    }

    /**
     * Gets the type of ingredient this storage provides
     */
    public String getIngredientType() {
        return ingredientType;
    }

    /**
     * Creates a new ingredient of the stored type
     * @return A new ingredient, or null if no type is set
     */
    public Ingredient createIngredient() {
        if (ingredientType == null || ingredientType.isEmpty()) {
            return null;
        }

        Ingredient ingredient = Ingredient.create(ingredientType);

        // Set default properties based on ingredient type
        // Pasta is special - it cannot be chopped
        if (!ingredientType.equalsIgnoreCase("Pasta")) {
            ingredient.setChoppable(true);
        }
        ingredient.setCookable(true);
        ingredient.setCanBePlacedOnPlate(true);

        System.out.println("IngredientStorage: Created new " + ingredientType);
        return ingredient;
    }
}


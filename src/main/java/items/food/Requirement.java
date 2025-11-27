package items.food;

public class Requirement {
    private String ingredientName;
    private IngredientState requiredState;

    public Requirement(String ingredientName, IngredientState requiredState) {
        this.ingredientName = ingredientName;
        this.requiredState = requiredState;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public IngredientState getRequiredState() {
        return requiredState;
    }
}



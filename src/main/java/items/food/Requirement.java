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
    
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public IngredientState getRequiredState() {
        return requiredState;
    }
    
    public void setRequiredState(IngredientState requiredState) {
        this.requiredState = requiredState;
    }
    
}
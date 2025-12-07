package items.food;
import java.util.List;

public class Recipe {
    private String name;
    private List<Requirement> requiredComponents;

    public Recipe(String name, List<Requirement> requiredComponents) {
        this.name = name;
        this.requiredComponents = requiredComponents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Requirement> getRequiredComponents() {
        return requiredComponents;
    }

    public void setRequiredComponents(List<Requirement> requiredComponents) {
        this.requiredComponents = requiredComponents;
    }

    public boolean validateDish(Dish dish) { 
        for (Requirement requirement : requiredComponents) {
            boolean found = false;
            for (Ingredient ingredient : dish.getIngredients()){
                if (ingredient.getName().equals(requirement.getIngredientName()) &&
                    ingredient.getState() == requirement.getRequiredState()) {
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
   
}


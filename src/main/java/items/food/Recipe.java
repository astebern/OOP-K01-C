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

    public List<Requirement> getRequiredComponents() {
        return requiredComponents;
    }

    public boolean validateDish(Dish dish) { //cek kebutuhan 1-1 loop requirement  yang ada di recipe
        for (Requirement req : requiredComponents) {
            if (!dish.hasComponent(req)) { //apakah dish memiliki ingridient yang sesuai requirement
                return false;
            }
        }
        return true;
    }  
}


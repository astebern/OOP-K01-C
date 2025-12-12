package items.food;

import items.Item;
import items.Preparable;
import java.util.ArrayList;
import java.util.List;

public class Dish extends Item {
    private String name;
    private List<Preparable> components;

    public Dish(String name, List<Preparable> components) {
        this.name = name;
        this.components = new ArrayList<>(components);
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
}
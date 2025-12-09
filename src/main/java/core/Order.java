package core;

import items.food.Dish;
import items.food.Recipe;

public class Order {
    private int posisiOrder;
    private Recipe recipe;
    private int reward;
    private int penalty;
    private int timeLimit;

    public Order(int posisiOrder, Recipe recipe, int reward, int penalty, int timeLimit) {
        this.posisiOrder = posisiOrder;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
        this.timeLimit = timeLimit;
    }
    
    public int getPosisiOrder() {
        return posisiOrder;
    }

    public void setPosisiOrder(int newPos) {
        this.posisiOrder = newPos;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe newRecipe) {
        this.recipe = newRecipe;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int newReward) {
        this.reward = newReward;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public boolean compareDishAndRecipe(Dish dish, Recipe recipe) {
        return recipe.validateDish(dish);
    }

    public boolean isExpired(){
        //kalau waktunya > waktu yg ditentukan 
        return false;
    }

}



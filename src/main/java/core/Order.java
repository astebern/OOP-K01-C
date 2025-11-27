package core;

public class Order {
    private int orderNumber;
    private Recipe recipe;
    private int reward;
    private int penalty;

    public Order(int orderNumber, Recipe recipe, int reward, int penalty) {
        this.orderNumber = orderNumber;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
    }

    
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public boolean isOrderCompleted(Dish dish) {
        return recipe.validateDish(dish);
    }

    @Override
    public String toString() {
        return "Order #" + orderNumber +
               " | Recipe: " + (recipe != null ? recipe.getName() : "None") +
               " | Reward: " + reward +
               " | Penalty: " + penalty;
    }
}



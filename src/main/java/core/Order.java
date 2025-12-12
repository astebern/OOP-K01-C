package core;

import items.food.Dish;
import items.food.Recipe;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Order {
    private int posisiOrder;
    private Recipe recipe;
    private int reward;
    private int penalty;
    private int timeLimit; // in seconds
    private long startTime;
    private long elapsedTime; // in milliseconds
    private BufferedImage dishImage; // Image of the dish to display

    public Order(int posisiOrder, Recipe recipe, int reward, int penalty, int timeLimit) {
        this.posisiOrder = posisiOrder;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
        this.timeLimit = timeLimit;
        this.startTime = 0;
        this.elapsedTime = 0;
        loadDishImage();
    }

    private void loadDishImage() {
        try {
            String imagePath = getDishImagePath(recipe.getName());
            if (imagePath != null) {
                this.dishImage = ImageIO.read(getClass().getResourceAsStream(imagePath));
            }
        } catch (IOException e) {
            System.err.println("Failed to load image for order: " + recipe.getName());
        } catch (Exception e) {
            System.err.println("Error loading image for order: " + recipe.getName());
        }
    }

    private String getDishImagePath(String recipeName) {
        switch (recipeName) {
            case "Pasta Marinara":
                return "/items/serving/marinara.png";
            case "Pasta Bolognese":
                return "/items/serving/bolognese.png";
            case "Pasta Ai Gamberetti":
                return "/items/serving/pasta_shrimp.png";
            case "Pasta Di Pesce":
                return "/items/serving/pasta_fish.png";
            case "Pasta Frutti di Mare":
                return "/items/serving/frutti.png";
            default:
                return null;
        }
    }

    public BufferedImage getDishImage() {
        return dishImage;
    }

    public void startTimer() {
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = 0;
    }

    public void updateTimer(long deltaTime) {
        if (startTime > 0) {
            elapsedTime += deltaTime;
        }
    }

    public int getRemainingTime() {
        if (startTime == 0) return timeLimit;
        long remainingMs = (timeLimit * 1000L) - elapsedTime;
        return Math.max(0, (int)(remainingMs / 1000));
    }

    public float getProgressPercent() {
        if (timeLimit == 0) return 0;
        return Math.min(100f, (elapsedTime / 1000f / timeLimit) * 100f);
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

    public int getTimeLimit() {
        return timeLimit;
    }

    public boolean compareDishAndRecipe(Dish dish, Recipe recipe) {
        return recipe.validateDish(dish);
    }

    public boolean isExpired(){
        return getRemainingTime() <= 0;
    }

}





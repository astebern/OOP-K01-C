package core;

import items.food.Dish;
import items.food.Recipe;

public class Order {
    private int posisiOrder;
    private Recipe recipe;
    private int reward;
    private int penalty;
    private int maxTime;      // Waktu awal (misal 60 detik)
    private float currentTimeout; // Waktu yang tersisa (float untuk presisi)

    public Order(int posisiOrder, Recipe recipe, int reward, int penalty, int timeLimit) {
        this.posisiOrder = posisiOrder;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
        this.maxTime = timeLimit * 60; // Asumsi input frame/tick (misal 60 tick = 1 detik)
        this.currentTimeout = this.maxTime;
    }

    // --- LOGIC BARU: Update Waktu ---
    public void decreaseTime() {
        if (currentTimeout > 0) {
            currentTimeout--; 
        }
    }

    public boolean isExpired() {
        return currentTimeout <= 0;
    }

    // Untuk GUI Bar: Mengembalikan persentase sisa waktu (0.0 - 1.0)
    public float getTimeProgress() {
        return currentTimeout / (float) maxTime;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public int getReward() { return reward; }
    public int getPenalty() { return penalty; }
}
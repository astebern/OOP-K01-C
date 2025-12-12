package core;

import items.food.Dish;
import items.food.Recipe;

public class Order {
    private int posisiOrder;
    private Recipe recipe;
    private int reward;
    private int penalty;
    
    // Atribut Waktu (Timer)
    private int maxTime;          // Waktu total (dalam frame/tick)
    private float currentTimeout; // Waktu tersisa

    public Order(int posisiOrder, Recipe recipe, int reward, int penalty, int timeLimit) {
        this.posisiOrder = posisiOrder;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;
        
        // Konversi detik ke frame (asumsi 60 FPS)
        // Jika timeLimit 60 detik -> 3600 frame
        this.maxTime = timeLimit * 60; 
        this.currentTimeout = this.maxTime;
    }
    
    // --- METHOD PENTING YANG HILANG SEBELUMNYA ---
    public int getPosisiOrder() {
        return posisiOrder;
    }

    public void setPosisiOrder(int newPos) {
        this.posisiOrder = newPos;
    }
    // ---------------------------------------------

    public Recipe getRecipe() {
        return recipe;
    }

    public int getReward() {
        return reward;
    }

    public int getPenalty() {
        return penalty;
    }

    // --- LOGIKA TIMER ---
    
    // Dipanggil setiap frame (tick) untuk mengurangi waktu
    public void decreaseTime() {
        if (currentTimeout > 0) {
            currentTimeout--; 
        }
    }

    public boolean isExpired(){
        return currentTimeout <= 0;
    }

    // Mengembalikan persentase sisa waktu (0.0 sampai 1.0) untuk GUI Bar
    public float getTimeProgress() {
        return currentTimeout / (float) maxTime;
    }

    public boolean compareDishAndRecipe(Dish dish, Recipe recipe) {
        return recipe.validateDish(dish);
    }
}
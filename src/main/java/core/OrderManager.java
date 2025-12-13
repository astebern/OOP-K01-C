package core;

import items.food.Dish;
import items.food.Recipe;
import stations.AssemblyStation;
import utils.BetterComments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderManager {
    private List<Order> activeOrders;
    private int maxConcurrentOrders = 3;
    private Random random;
    private int score;
    private int money;
    private int ordersCompleted;
    private int ordersFailed;
    private long lastUpdateTime;
    private boolean gameOver;
    private String gameOverReason;
    private static final int MAX_FAILED_ORDERS = 3;

    private long lastSpawnTime;
    private static final long SPAWN_INTERVAL = 15000; 

    // Stage timer fields
    private long stageStartTime;
    private long stageTimeLimit; // in milliseconds
    private static final long DEFAULT_STAGE_TIME = 5 * 60 * 1000; // 5 minutes in milliseconds

    @BetterComments(description="Initializes the order manager with random orders", type="constructor")
    public OrderManager() {
        this.activeOrders = new ArrayList<>();
        this.random = new Random();
        this.score = 0;
        this.money = 0;
        this.ordersCompleted = 0;
        this.ordersFailed = 0;
        this.lastUpdateTime = System.currentTimeMillis();
        this.gameOver = false;
        this.gameOverReason = "";

        // Initialize stage timer
        this.stageTimeLimit = DEFAULT_STAGE_TIME;
        this.stageStartTime = System.currentTimeMillis();
        this.lastSpawnTime = System.currentTimeMillis();

        // Generate initial orders
        generateNewOrder(0); 
        System.out.println("OrderManager: Initial order generated.");
    }

    @BetterComments(description="Generates a new random order from available recipes", type="method")
    private void generateNewOrder(int position) {
        List<Recipe> allRecipes = AssemblyStation.getRecipes();
        if (allRecipes.isEmpty()) {
            return;
        }

        // Pick a random recipe
        Recipe randomRecipe = allRecipes.get(random.nextInt(allRecipes.size()));

        // Calculate time limit based on recipe complexity (number of ingredients)
        int ingredientCount = randomRecipe.getRequiredComponents().size();
        int timeLimit = 60 + (ingredientCount * 15); // 60s base + 15s per ingredient

        // Calculate reward based on complexity
        int reward = 50 * ingredientCount;
        int penalty = 25 * ingredientCount;

        Order newOrder = new Order(position, randomRecipe, reward, penalty, timeLimit);
        newOrder.startTimer(); // Start the timer
        activeOrders.add(newOrder);

        System.out.println("OrderManager: Generated new order - " + randomRecipe.getName() +
                         " (Time: " + timeLimit + "s, Reward: $" + reward + ")");
    }

    @BetterComments(description="Updates order timers and handles expired orders", type="method")
    public void update() {
        if (gameOver) return; // jgn update if game over

        long currentTime = System.currentTimeMillis();
        long deltaTime = currentTime - lastUpdateTime;

        lastUpdateTime = currentTime;
        if (activeOrders.size() < maxConcurrentOrders && 
            currentTime - lastSpawnTime > SPAWN_INTERVAL) {
            
            boolean[] slotOccupied = new boolean[maxConcurrentOrders];
            for (Order order : activeOrders) {
                if (order.getPosisiOrder() >= 0 && order.getPosisiOrder() < maxConcurrentOrders) {
                    slotOccupied[order.getPosisiOrder()] = true;
                }
            }
            
            int freeSlot = -1;
            for (int i = 0; i < maxConcurrentOrders; i++) {
                if (!slotOccupied[i]) {
                    freeSlot = i;
                    break;
                }
            }

            if (freeSlot != -1) {
                generateNewOrder(freeSlot);
                lastSpawnTime = currentTime; // Reset timer spawn
                System.out.println("OrderManager: Spawned new order at position " + freeSlot);
            }
        }

        // Check if stage time has run out
        long elapsedTime = currentTime - stageStartTime;
        if (elapsedTime >= stageTimeLimit) {
            gameOver = true;
            gameOverReason = "Time's up! Stage time limit reached (5 minutes)";
            System.out.println("OrderManager: GAME OVER - " + gameOverReason);
            return;
        }

        // Check for expired orders
        List<Order> expiredOrders = new ArrayList<>();
        for (Order order : activeOrders) {
            order.updateTimer(deltaTime);

            if (order.isExpired()) {
                expiredOrders.add(order);
            }
        }

        // Handle expired orders
        for (Order expiredOrder : expiredOrders) {
            System.out.println("OrderManager: Order expired - " + expiredOrder.getRecipe().getName());
            money -= expiredOrder.getPenalty(); // Apply penalty
            if (money < 0) money = 0;

            ordersFailed++; // Increment failed orders
            System.out.println("OrderManager: Failed orders: " + ordersFailed + "/" + MAX_FAILED_ORDERS);

            int position = expiredOrder.getPosisiOrder();
            activeOrders.remove(expiredOrder);

            // Check if game over due to failed orders
            if (ordersFailed >= MAX_FAILED_ORDERS) {
                gameOver = true;
                gameOverReason = "Too many failed orders! (" + MAX_FAILED_ORDERS + " orders expired)";
                System.out.println("OrderManager: GAME OVER - " + gameOverReason);
                return; // Don't generate new orders
            }

            // Generate a new order in the same position
            generateNewOrder(position);
        }
    }

    @BetterComments(description="Checks if a dish matches any active order and completes it", type="method")
    public boolean checkAndCompleteOrder(Dish dish) {
        if (gameOver) return false; // Don't accept orders if game is over

        for (Order order : activeOrders) {
            if (order.compareDishAndRecipe(dish, order.getRecipe())) {
                System.out.println("OrderManager: Order completed - " + order.getRecipe().getName() +
                                 " (Reward: $" + order.getReward() + ")");

                // Award points and money
                money += order.getReward();
                score += order.getReward();
                ordersCompleted++;

                int position = order.getPosisiOrder();
                activeOrders.remove(order);

                // Generate a new order in the same position
                generateNewOrder(position);

                return true;
            }
        }

        // Dish doesn't match any order - still accept the plate but no score
        System.out.println("OrderManager: Dish not on menu - plate accepted but no score awarded");
        return false; // Return false to indicate no order was completed
    }

    // Getters
    public List<Order> getActiveOrders() {
        return new ArrayList<>(activeOrders);
    }

    public int getScore() {
        return score;
    }

    public int getMoney() {
        return money;
    }

    public int getOrdersCompleted() {
        return ordersCompleted;
    }

    public int getMaxConcurrentOrders() {
        return maxConcurrentOrders;
    }

    public int getOrdersFailed() {
        return ordersFailed;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getGameOverReason() {
        return gameOverReason;
    }

    public int getMaxFailedOrders() {
        return MAX_FAILED_ORDERS;
    }

    @BetterComments(description="Gets remaining stage time in seconds", type="method")
    public int getStageRemainingTime() {
        if (gameOver) return 0;
        long elapsed = System.currentTimeMillis() - stageStartTime;
        long remaining = stageTimeLimit - elapsed;
        return Math.max(0, (int)(remaining / 1000));
    }

    @BetterComments(description="Gets stage time progress as percentage", type="method")
    public float getStageProgressPercent() {
        long elapsed = System.currentTimeMillis() - stageStartTime;
        return Math.min(100f, (elapsed * 100f / stageTimeLimit));
    }

    @BetterComments(description="Gets total stage time limit in seconds", type="method")
    public int getStageTimeLimit() {
        return (int)(stageTimeLimit / 1000);
    }
}


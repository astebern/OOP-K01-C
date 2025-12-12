package core;

import items.food.Dish;
import items.food.IngredientState;
import items.food.Recipe;
import items.food.Requirement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class OrderManager {
    private GameMaster gameMaster;
    private List<Order> activeOrders;
    private Queue<Order> pendingOrders; // Antrian pesanan yang belum muncul
    private int score = 0;
    
    // Level Timer (dalam frame, asumsi 60 FPS)
    private int levelTimeLimit; 
    private int currentLevelTime;

    // Logic Spawn Berurutan
    private int spawnTimer = 0;
    private int nextSpawnDelay = 0;
    private Random random = new Random();

    // Definisi Resep
    private Recipe pastaMarinara;
    private Recipe pastaBolognese;
    private Recipe pastaFrutti;

    public OrderManager(GameMaster gm) {
        this.gameMaster = gm;
        this.activeOrders = new ArrayList<>();
        this.pendingOrders = new LinkedList<>();
        initializeRecipes();
    }

    private void initializeRecipes() {
        // 1. Pasta Marinara: Pasta (Cooked) + Tomat (Cooked)
        List<Requirement> r1 = new ArrayList<>();
        r1.add(new Requirement("Pasta", IngredientState.COOKED));
        r1.add(new Requirement("Tomat", IngredientState.COOKED));
        pastaMarinara = new Recipe("Pasta Marinara", r1);

        // 2. Pasta Bolognese: Pasta (Cooked) + Daging (Cooked)
        List<Requirement> r2 = new ArrayList<>();
        r2.add(new Requirement("Pasta", IngredientState.COOKED));
        r2.add(new Requirement("Daging", IngredientState.COOKED));
        pastaBolognese = new Recipe("Pasta Bolognese", r2);

        // 3. Frutti di Mare: Pasta (Cooked) + Udang (Cooked) + Ikan (Cooked)
        List<Requirement> r3 = new ArrayList<>();
        r3.add(new Requirement("Pasta", IngredientState.COOKED));
        r3.add(new Requirement("Udang", IngredientState.COOKED));
        r3.add(new Requirement("Ikan", IngredientState.COOKED));
        pastaFrutti = new Recipe("Frutti di Mare", r3);
    }

    // Load konfigurasi resep berdasarkan stage
    public void loadStage(int stage) {
        activeOrders.clear();
        pendingOrders.clear();
        score = 0;
        spawnTimer = 0;

        if (stage == 1) {
            // Stage 1: 1 Marinara, 1 Bolognese, 1 Frutti (2 Menit)
            levelTimeLimit = 2 * 60 * 60; // 2 menit * 60 detik * 60 FPS
            addPendingOrder(pastaMarinara);
            addPendingOrder(pastaBolognese);
            addPendingOrder(pastaFrutti);
        } 
        else if (stage == 2) {
            // Stage 2: 2 Marinara, 1 Bolognese, 2 Frutti (2 Menit)
            levelTimeLimit = 2 * 60 * 60;
            addPendingOrder(pastaMarinara); addPendingOrder(pastaMarinara);
            addPendingOrder(pastaBolognese);
            addPendingOrder(pastaFrutti); addPendingOrder(pastaFrutti);
        } 
        else if (stage == 3) {
            // Stage 3: 2 Marinara, 2 Bolognese, 2 Frutti (1.5 Menit)
            levelTimeLimit = 90 * 60; // 90 detik * 60 FPS
            addPendingOrder(pastaMarinara); addPendingOrder(pastaMarinara);
            addPendingOrder(pastaBolognese); addPendingOrder(pastaBolognese);
            addPendingOrder(pastaFrutti); addPendingOrder(pastaFrutti);
        } 
        else if (stage == 4) {
            // Stage 4: 3 Marinara, 3 Bolognese, 3 Frutti (2 Menit)
            levelTimeLimit = 2 * 60 * 60;
            for(int i=0; i<3; i++) addPendingOrder(pastaMarinara);
            for(int i=0; i<3; i++) addPendingOrder(pastaBolognese);
            for(int i=0; i<3; i++) addPendingOrder(pastaFrutti);
        }

        currentLevelTime = levelTimeLimit;
        
        // Spawn 1 order pertama langsung
        spawnNextOrder();
        setNextSpawnDelay();
    }

    private void addPendingOrder(Recipe recipe) {
        int patienceTime = 45; // 45 Detik waktu tunggu per pelanggan
        // ID order sementara 0, nanti diupdate saat masuk activeOrders
        Order o = new Order(0, recipe, 100, 50, patienceTime);
        pendingOrders.add(o);
    }

    // Set delay acak untuk spawn berikutnya (antara 300 - 600 frames / 5-10 detik)
    private void setNextSpawnDelay() {
        nextSpawnDelay = 300 + random.nextInt(300); 
    }

    public void update() {
        // 1. Kurangi Level Timer
        if (currentLevelTime > 0) {
            currentLevelTime--;
        } else {
            // Waktu Habis -> Cek apakah masih ada order tersisa
            if (!activeOrders.isEmpty() || !pendingOrders.isEmpty()) {
                gameMaster.levelFailed();
            }
            return;
        }

        // 2. Logic Spawn Berurutan
        // Spawn hanya jika slot active < 4 DAN masih ada pending order
        if (activeOrders.size() < 4 && !pendingOrders.isEmpty()) {
            spawnTimer++;
            if (spawnTimer >= nextSpawnDelay) {
                spawnNextOrder();
                spawnTimer = 0;
                setNextSpawnDelay();
            }
        }

        // 3. Update Timer untuk Order yang sedang aktif
        List<Order> expiredOrders = new ArrayList<>();
        for (Order order : activeOrders) {
            order.decreaseTime();
            if (order.isExpired()) {
                expiredOrders.add(order);
            }
        }

        // 4. Handle Order yang Expired
        for (Order o : expiredOrders) {
            System.out.println("Order Expired: " + o.getRecipe().getName());
            score -= o.getPenalty();
            activeOrders.remove(o);
            
            // Masukkan kembali ke antrian belakang (Retry)
            addPendingOrder(o.getRecipe()); 
            
            // Reset spawn timer biar tidak langsung muncul
            spawnTimer = 0; 
        }
        
        // Update posisi visual di GUI
        updateOrderPositions();

        // 5. Cek Win Condition (Semua habis & terselesaikan)
        if (activeOrders.isEmpty() && pendingOrders.isEmpty()) {
            gameMaster.levelCleared();
        }
    }

    private void spawnNextOrder() {
        if (!pendingOrders.isEmpty()) {
            Order nextOrder = pendingOrders.poll();
            nextOrder.setPosisiOrder(activeOrders.size());
            activeOrders.add(nextOrder);
            System.out.println("New Order Spawned: " + nextOrder.getRecipe().getName());
        }
    }
    
    private void updateOrderPositions() {
        for (int i = 0; i < activeOrders.size(); i++) {
            activeOrders.get(i).setPosisiOrder(i);
        }
    }

    public boolean deliverDish(Dish dish) {
        for (Order order : activeOrders) {
            if (order.getRecipe().validateDish(dish)) {
                System.out.println("Order Success! " + dish.getName());
                score += order.getReward();
                activeOrders.remove(order);
                updateOrderPositions();
                return true;
            }
        }
        System.out.println("Wrong Dish! Penalty applied.");
        score -= 10;
        return false;
    }

    // Getters
    public List<Order> getActiveOrders() { return activeOrders; }
    public int getScore() { return score; }
    public int getLevelTimeRemaining() { return currentLevelTime; }
    public int getLevelTimeLimit() { return levelTimeLimit; }
}
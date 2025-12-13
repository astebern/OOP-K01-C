package core;

import entities.Chef;
import map.GameMap;
import utils.BetterComments;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GamePanel extends JPanel implements Runnable{
    final int originalTilSize = 16;
    final int scale = 6;
    public final int tileSize = originalTilSize * scale;
    final int columns = 19;
    final int rows = 10;
    final int screenWidth = columns * tileSize;
    final int screenHeight = rows * tileSize;

    double fps = 60;

    GameMap tileM = new GameMap(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    GameMaster gameMaster;


    @BetterComments(description = "Sets up rendering and input and spawn chefs",type="constructor")
    public GamePanel(GameMaster gameMaster){
        this.gameMaster = gameMaster;
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        this.setFocusTraversalKeysEnabled(false);

        Chef chef1 = new Chef(this, keyH, tileM, 2 * tileSize, 3 * tileSize);
        chef1.setName("Chef 1");

        Chef chef2 = new Chef(this, keyH, tileM, 11 * tileSize, 6 * tileSize);
        chef2.setName("Chef 2");

        gameMaster.addChef(chef1);
        gameMaster.addChef(chef2);
    }

    @BetterComments(description = "Creates game loop on seperate thread",type="method")
    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @BetterComments(description = "Stops the game loop", type="method")
    public void stopGameThread() {
        gameThread = null; // Ini akan membuat kondisi while(gameThread != null) menjadi false
    }

@Override
public void run() {
    double drawInterval = 1000000000 / fps;
    double nextDrawTime = System.nanoTime() + drawInterval;

    while (gameThread != null) { 
        update();
        repaint();
        try {
            double remainingTime = (nextDrawTime - System.nanoTime()) / 1_000_000;
            if (remainingTime < 0) {
                remainingTime = 0;
            }

            Thread.sleep((long)remainingTime);
            nextDrawTime += drawInterval;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

    @BetterComments(description = "Updates Active Chef",type="method")
    public void update(){
        // Check if ESC pressed during game over
        OrderManager orderManager = gameMaster.getOrderManager();
        if (orderManager != null && orderManager.isGameOver() && keyH.escPressed) {
            keyH.escPressed = false;
            gameMaster.showStartMenu();
            return;
        }

        if (keyH.switchChef) {
            System.out.println("GamePanel.update() detected switchChef flag, calling gameMaster.switchChef()");
            gameMaster.switchChef();
            keyH.switchChef = false;
        }

        // Only update the active chef
        Chef activeChef = gameMaster.getActiveChef();
        if (activeChef != null) {
            activeChef.update();
        }

        // Update order manager (timers and expired orders)
        if (orderManager != null) {
            orderManager.update();
        }
    }

    @BetterComments(description = "Renders the game map, all chefs, and a visual indicator around the currently active chef.",type="method")
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tileM.draw(g2);

        // Draw all chefs
        for (Chef chef : gameMaster.getAllChefs()) {
            chef.draw(g2);
        }

        // Draw indicator for active chef
        Chef activeChef = gameMaster.getActiveChef();
        if (activeChef != null) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            int x = activeChef.getPosition().getX();
            int y = activeChef.getPosition().getY();
            g2.drawRect(x - 2, y - 2, tileSize + 4, tileSize + 4);

            // Draw indicator for target tile (tile in front of chef)
            int targetTileX = activeChef.getTargetTileX();
            int targetTileY = activeChef.getTargetTileY();
            if (targetTileX >= 0 && targetTileX < tileM.getMapWidth() &&
                targetTileY >= 0 && targetTileY < tileM.getMapHeight()) {
                g2.setColor(Color.GREEN);
                g2.setStroke(new BasicStroke(2));
                int targetX = targetTileX * tileSize;
                int targetY = targetTileY * tileSize;
                g2.drawRect(targetX + 2, targetY + 2, tileSize - 4, tileSize - 4);
            }
        }

        // Draw orders on the right side
        drawOrders(g2);

        // Draw game over screen if game is over
        OrderManager orderManager = gameMaster.getOrderManager();
        if (orderManager != null && orderManager.isGameOver()) {
            drawGameOver(g2, orderManager);
        }

        g2.dispose();
    }

    @BetterComments(description = "Draws stage timer at the top of order area", type="method")
    private void drawStageTimer(Graphics2D g2, OrderManager orderManager, int x, int width) {
        int timerHeight = (int)(tileSize * 0.7);
        int timerY = 10;

        // Draw timer panel
        g2.setColor(new Color(60, 60, 60, 230));
        g2.fillRoundRect(x + 10, timerY, width - 20, timerHeight, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x + 10, timerY, width - 20, timerHeight, 10, 10);

        // Get timer info
        int remainingSeconds = orderManager.getStageRemainingTime();
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        float progress = orderManager.getStageProgressPercent();

        // Title
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("STAGE TIMER", x + 25, timerY + 25);

        // Time display
        String timeText = String.format("%d:%02d", minutes, seconds);
        g2.setFont(new Font("Arial", Font.BOLD, 24));

        // Color based on remaining time
        if (remainingSeconds > 120) {
            g2.setColor(new Color(50, 220, 50)); // Green
        } else if (remainingSeconds > 60) {
            g2.setColor(new Color(255, 180, 0)); // Orange
        } else {
            g2.setColor(Color.RED); // Red
        }

        int timeWidth = g2.getFontMetrics().stringWidth(timeText);
        g2.drawString(timeText, x + (width - timeWidth) / 2, timerY + 55);
    }

    @BetterComments(description = "Draws game over screen with final stats", type="method")
    private void drawGameOver(Graphics2D g2, OrderManager orderManager) {
        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        // Game Over title
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 60));
        String gameOverText = "GAME OVER";
        int titleWidth = g2.getFontMetrics().stringWidth(gameOverText);
        g2.drawString(gameOverText, (screenWidth - titleWidth) / 2, screenHeight / 2 - 100);

        // Reason
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String reason = orderManager.getGameOverReason();
        int reasonWidth = g2.getFontMetrics().stringWidth(reason);
        g2.drawString(reason, (screenWidth - reasonWidth) / 2, screenHeight / 2 - 30);

        // Final stats panel
        int panelWidth = 400;
        int panelHeight = 250;
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = screenHeight / 2 + 20;

        g2.setColor(new Color(40, 40, 40, 230));
        g2.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

        // Stats
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.drawString("FINAL STATS", panelX + 110, panelY + 40);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 22));
        g2.drawString("Final Score: " + orderManager.getScore(), panelX + 30, panelY + 85);
        g2.drawString("Final Money: $" + orderManager.getMoney(), panelX + 30, panelY + 120);
        g2.drawString("Orders Completed: " + orderManager.getOrdersCompleted(), panelX + 30, panelY + 155);
        g2.drawString("Orders Failed: " + orderManager.getOrdersFailed(), panelX + 30, panelY + 190);

        // Instruction to exit
        g2.setColor(Color.LIGHT_GRAY);
        g2.setFont(new Font("Arial", Font.ITALIC, 18));
        String instruction = "Press ESC to exit";
        int instWidth = g2.getFontMetrics().stringWidth(instruction);
        g2.drawString(instruction, (screenWidth - instWidth) / 2, screenHeight - 50);
    }

    @BetterComments(description = "Draws active orders on the right side of the screen (x > 14)", type="method")
    private void drawOrders(Graphics2D g2) {
        OrderManager orderManager = gameMaster.getOrderManager();
        if (orderManager == null) return;

        // Draw white background for all tiles where x > 14
        g2.setColor(Color.WHITE);
        int orderAreaX = 15 * tileSize;
        int orderAreaWidth = 4 * tileSize; // x=15,16,17,18 (4 tiles)
        int orderAreaHeight = 10 * tileSize; // All 10 rows
        g2.fillRect(orderAreaX, 0, orderAreaWidth, orderAreaHeight);

        // Draw stage timer at the top
        drawStageTimer(g2, orderManager, orderAreaX, orderAreaWidth);

        List<Order> activeOrders = orderManager.getActiveOrders();

        // Draw each order vertically (stacked on top of each other)
        // Total space: 10 tiles tall, 4 tiles wide
        // 0.8 tiles for stage timer, then 3 orders + 1 stats
        int orderStartY = (int)(tileSize * 0.8) + 10; // Start after stage timer
        for (int i = 0; i < activeOrders.size(); i++) {
            Order order = activeOrders.get(i);

            // Each order gets 2.3 tiles of space
            int orderX = 15 * tileSize + 10; // Start at x=15 tiles + 10px margin
            int orderY = orderStartY + (i * (int)(tileSize * 2.3)); // 2.3 tiles per order

            int panelWidth = tileSize * 4 - 20; // 4 tiles wide minus margins
            int panelHeight = (int)(tileSize * 2.1); // 2.1 tiles tall

            // Draw background panel for order
            g2.setColor(new Color(50, 50, 50, 220)); // Semi-transparent dark background
            g2.fillRoundRect(orderX, orderY, panelWidth, panelHeight, 15, 15);

            // Draw border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(orderX, orderY, panelWidth, panelHeight, 15, 15);

            int contentX = orderX + 12;
            int contentY = orderY + 12;

            // Draw dish image
            if (order.getDishImage() != null) {
                int imageSize = tileSize + 10;
                g2.drawImage(order.getDishImage(), contentX, contentY, imageSize, imageSize, null);
            }

            // Draw recipe name (next to image)
            int textX = contentX + tileSize + 25;
            int textY = contentY + 18;

            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            String recipeName = order.getRecipe().getName();
            g2.drawString(recipeName, textX, textY);

            // Draw ingredients list
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            List<items.food.Requirement> requirements = order.getRecipe().getRequiredComponents();
            int ingredientY = textY + 18;

            for (items.food.Requirement req : requirements) {
                String ingredientText = "• " + req.getIngredientName() + " (" + req.getRequiredState() + ")";
                g2.drawString(ingredientText, textX, ingredientY);
                ingredientY += 16;
            }

            // Draw timer section at bottom
            int timerY = orderY + panelHeight - 45;

            // Timer label
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString("Time:", contentX, timerY);

            // Draw timer bar
            int remainingTime = order.getRemainingTime();
            float progress = order.getProgressPercent();

            int barWidth = panelWidth - 90;
            int barHeight = 18;
            int barX = contentX + 50;
            int barY = timerY - 13;

            // Background bar
            g2.setColor(new Color(80, 80, 80));
            g2.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);

            // Progress bar (color changes based on time remaining)
            int filledWidth = (int)(barWidth * (1 - progress / 100f));
            if (remainingTime > 30) {
                g2.setColor(new Color(50, 220, 50)); // Green
            } else if (remainingTime > 10) {
                g2.setColor(new Color(255, 180, 0)); // Orange
            } else {
                g2.setColor(new Color(255, 50, 50)); // Red
            }
            g2.fillRoundRect(barX, barY, filledWidth, barHeight, 8, 8);

            // Timer text overlay
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String timeText = remainingTime + "s";
            int timeTextWidth = g2.getFontMetrics().stringWidth(timeText);
            g2.drawString(timeText, barX + (barWidth - timeTextWidth) / 2, barY + 13);

            // Draw reward at bottom
            int rewardY = timerY + 22;
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Reward: $" + order.getReward(), contentX, rewardY);
        }

        // Draw score and money - positioned after stage timer and 3 orders
        // 0.8 tiles (timer) + 3 orders × 2.3 tiles = 7.7 tiles used
        int statsX = 15 * tileSize + 10;
        int statsY = orderStartY + (int)(3 * tileSize * 2.3) + 10; // After 3 orders
        int statsWidth = tileSize * 4 - 20;
        int statsHeight = (int)(tileSize * 2.0); // Fit in remaining space

        g2.setColor(new Color(40, 40, 40, 220));
        g2.fillRoundRect(statsX, statsY, statsWidth, statsHeight, 10, 10);

        // Title
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("GAME STATS", statsX + 15, statsY + 25);

        // Stats
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        g2.drawString("Score: " + orderManager.getScore(), statsX + 15, statsY + 50);
        g2.drawString("Money: $" + orderManager.getMoney(), statsX + 15, statsY + 75);
        g2.drawString("Completed: " + orderManager.getOrdersCompleted(), statsX + 15, statsY + 100);

        // Failed orders with color coding
        int failedCount = orderManager.getOrdersFailed();
        int maxFailed = orderManager.getMaxFailedOrders();
        if (failedCount >= maxFailed - 1) {
            g2.setColor(Color.RED); // Critical - one more failure = game over
        } else if (failedCount > 0) {
            g2.setColor(Color.ORANGE); // Warning
        } else {
            g2.setColor(Color.WHITE); // All good
        }
        g2.drawString("Failed: " + failedCount + "/" + maxFailed, statsX + 15, statsY + 125);
    }

    public GameMaster getGameMaster() {
        return gameMaster;
    }
}

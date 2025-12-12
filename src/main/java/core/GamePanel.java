package core;

import entities.Chef;
import map.GameMap;
import utils.BetterComments;
import core.Order; 
import java.util.List;

import javax.swing.*;
import java.awt.*;

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

        // Spawn Chefs
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

    @BetterComments(description = "Updates Game Logic",type="method")
    public void update(){
        if (keyH.switchChef) {
            gameMaster.switchChef();
            keyH.switchChef = false;
        }

        // [BARU] Update Order Manager (Timer level berjalan di sini)
        gameMaster.getOrderManager().update();

        // Update Active Chef
        Chef activeChef = gameMaster.getActiveChef();
        if (activeChef != null) {
            activeChef.update();
        }
    }

    @BetterComments(description = "Renders the game map, chefs, and UI",type="method")
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        // 1. Gambar Map
        tileM.draw(g2);

        // 2. Gambar Chef
        for (Chef chef : gameMaster.getAllChefs()) {
            chef.draw(g2);
        }

        // 3. Gambar Indikator Chef
        Chef activeChef = gameMaster.getActiveChef();
        if (activeChef != null) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(3));
            int x = activeChef.getPosition().getX();
            int y = activeChef.getPosition().getY();
            g2.drawRect(x - 2, y - 2, tileSize + 4, tileSize + 4);

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

        // 4. [BARU] Gambar UI Order di Kanan
        drawOrderUI(g2);

        g2.dispose();
    }

    // [BARU] Method menggambar panel kanan (Order & Timer)
    private void drawOrderUI(Graphics2D g2) {
        int uiStartX = 14 * tileSize; 
        int uiWidth = screenWidth - uiStartX;
        
        // Background Panel Transparan
        g2.setColor(new Color(40, 40, 40, 230)); 
        g2.fillRect(uiStartX, 0, uiWidth, screenHeight);
        
        // --- LEVEL TIMER ---
        int remainingFrames = gameMaster.getOrderManager().getLevelTimeRemaining();
        int totalFrames = gameMaster.getOrderManager().getLevelTimeLimit();
        int totalSeconds = remainingFrames / 60;
        String timeString = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString(timeString, uiStartX + 120, 40); // Timer di pojok kanan atas

        // Header Order & Score
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("ORDERS", uiStartX + 20, 40);
        g2.setColor(Color.YELLOW);
        g2.drawString("Score: " + gameMaster.getOrderManager().getScore(), uiStartX + 20, screenHeight - 30);

        // Loop Gambar Kartu Order
        List<Order> orders = gameMaster.getOrderManager().getActiveOrders();
        int yPos = 60; 

        for (Order order : orders) {
            drawSingleOrderCard(g2, order, uiStartX + 10, yPos);
            yPos += 120; 
        }
    }

    private void drawSingleOrderCard(Graphics2D g2, Order order, int x, int y) {
        int cardWidth = (screenWidth - x) - 15;
        int cardHeight = 100;

        // Card Background
        g2.setColor(new Color(245, 245, 220));
        g2.fillRoundRect(x, y, cardWidth, cardHeight, 15, 15);
        
        // Nama Resep
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString(order.getRecipe().getName(), x + 15, y + 30);

        // Reward Info
        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Reward: " + order.getReward(), x + 15, y + 50);

        // Progress Bar (Sisa Waktu Order)
        float progress = order.getTimeProgress(); 
        
        int barWidth = cardWidth - 30;
        int barHeight = 12;
        int barX = x + 15;
        int barY = y + 75;

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        // Warna Bar Berubah (Hijau -> Kuning -> Merah)
        if (progress > 0.5f) g2.setColor(new Color(0, 180, 0));
        else if (progress > 0.25f) g2.setColor(Color.ORANGE);
        else g2.setColor(Color.RED);

        int currentBarWidth = (int) (barWidth * progress);
        g2.fillRect(barX, barY, currentBarWidth, barHeight);
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(barX, barY, barWidth, barHeight);
    }
}
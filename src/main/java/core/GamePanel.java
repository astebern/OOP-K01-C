package core;

import entities.Chef;
import map.GameMap;
import utils.BetterComments;
import utils.Position;

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

    @BetterComments(description = "Updates Active Chef",type="method")
    public void update(){
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

        g2.dispose();
    }
}




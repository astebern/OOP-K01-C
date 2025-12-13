package core;

import entities.Chef;
import utils.BetterComments;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameMaster {
    private static GameMaster instance;

    private Boolean isGameRunning;
    private List<Chef> chefList;
    private int activeChefIndex;
    private GamePanel gamePanel;
    private JFrame frame;
    private StartMenu startMenu;
    private StageMenu stageMenu;
    private HowToPlayMenu howToPlayMenu;
    private OrderManager orderManager;

    @BetterComments(description="Private constructor for Singleton pattern",type="constructor")
    private GameMaster(){
        this.isGameRunning = true;
        this.chefList = new ArrayList<>();
        this.activeChefIndex = 0;
    }

    @BetterComments(description="Get the singleton instance of GameMaster",type="method")
    public static GameMaster getInstance(){
        if(instance == null){
            instance = new GameMaster();
        }
        return instance;
    }

    @BetterComments(description="creates game window",type="method")
    public void main(){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("NimonsCooked");

        startMenu = new StartMenu(this);
        frame.add(startMenu);

        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @BetterComments(description="Show stage selection menu",type="method")
    public void showStageMenu(){
        if (gamePanel != null) {
            gamePanel.stopGameThread();
            gamePanel = null; 
        }

        frame.getContentPane().removeAll();

        stageMenu = new StageMenu(this);
        frame.add(stageMenu);

        frame.revalidate();
        frame.repaint();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    @BetterComments(description="Go back to start menu",type="method")
    public void showStartMenu(){
        frame.getContentPane().removeAll();

        startMenu = new StartMenu(this);
        frame.add(startMenu);

        frame.revalidate();
        frame.repaint();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    @BetterComments(description="Show how to play menu",type="method")
    public void showHowToPlayMenu(){
        frame.getContentPane().removeAll();

        howToPlayMenu = new HowToPlayMenu(this);
        frame.add(howToPlayMenu);

        frame.revalidate();
        frame.repaint();
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    @BetterComments(description="Replace the stage menu and initialize the main gameplay panel",type="method")
    public void startGame(int stageNumber){

        //// ini yg fix bug new stage dobel player      
        if (gamePanel != null) {
            gamePanel.stopGameThread();
        }
        chefList.clear(); 
        activeChefIndex = 0; 

        frame.getContentPane().removeAll();

        orderManager = new OrderManager();

        // Set target score based on stage number
        int targetScore = getTargetScoreForStage(stageNumber);
        orderManager.setTargetScore(targetScore);

        // Set stage time limit based on stage number
        long timeLimit = getTimeLimitForStage(stageNumber);
        orderManager.setStageTimeLimit(timeLimit);

        System.out.println("GameMaster: Stage " + stageNumber + " - Target Score: " + targetScore + ", Time: " + (timeLimit/1000) + "s");

        gamePanel = new GamePanel(this);
        frame.add(gamePanel);

        frame.revalidate();
        frame.repaint();
        frame.pack();
        frame.setLocationRelativeTo(null);

        gamePanel.startGameThread();
        gamePanel.requestFocusInWindow();
    }

    @BetterComments(description="Returns the target score for a specific stage",type="method")
    private int getTargetScoreForStage(int stageNumber) {
        switch (stageNumber) {
            case 1:
                return 100;  // Stage 1: Tutorial - 100 points (~2 orders)
            case 2:
                return 200;  // Stage 2: Easy - 200 points (~4 orders)
            case 3:
                return 350;  // Stage 3: Medium - 350 points (~7 orders)
            case 4:
                return 500;  // Stage 4: Hard - 500 points (~10 orders)
            default:
                return 200;  // Default - 200 points
        }
    }

    @BetterComments(description="Returns the time limit in milliseconds for a specific stage",type="method")
    private long getTimeLimitForStage(int stageNumber) {
        switch (stageNumber) {
            case 1:
                return 2 * 60 * 1000;  // Stage 1: 2 minutes (120 seconds)
            case 2:
                return 3 * 60 * 1000;  // Stage 2: 3 minutes (180 seconds)
            case 3:
                return 4 * 60 * 1000;  // Stage 3: 4 minutes (240 seconds)
            case 4:
                return 5 * 60 * 1000;  // Stage 4: 5 minutes (300 seconds)
            default:
                return 3 * 60 * 1000;  // Default: 3 minutes
        }
    }

    @BetterComments(description = "Cycles to the next available chef and updates the active chef index",type ="method")
    public void switchChef() {
        if (chefList.size() > 1) {
            activeChefIndex = (activeChefIndex + 1) % chefList.size();
            System.out.println("Switched to Chef " + (activeChefIndex + 1));
        }
    }

    //Getter and Setter Functions
    public void addChef(Chef chef) {
        chefList.add(chef);
    }

    public List<Chef> getAllChefs() {
        return chefList;
    }

    public void setIsGameRunning(boolean bol){
        this.isGameRunning = bol;
    }

    public boolean getIsGameRunning(){
        return this.isGameRunning;
    }

    public Chef getActiveChef() {
        if (chefList.isEmpty()) {
            return null;
        }
        return chefList.get(activeChefIndex);
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    @BetterComments(description="Get all chefs except the specified one for collision checking",type="method")
    public List<Chef> getOtherChefs(Chef currentChef) {
        List<Chef> otherChefs = new ArrayList<>();
        for (Chef chef : chefList) {
            if (chef != currentChef) {
                otherChefs.add(chef);
            }
        }
        return otherChefs;
    }
}

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
    public void startGame(){
        frame.getContentPane().removeAll();

        gamePanel = new GamePanel(this);
        frame.add(gamePanel);

        frame.revalidate();
        frame.repaint();
        frame.pack();
        frame.setLocationRelativeTo(null);

        gamePanel.startGameThread();
        gamePanel.requestFocusInWindow();
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





}


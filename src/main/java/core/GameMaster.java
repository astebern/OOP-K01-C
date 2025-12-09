package core;

import entities.Chef;
import utils.BetterComments;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameMaster {
    private Boolean isGameRunning;
    private List<Chef> chefList;
    private int activeChefIndex;
    private GamePanel gamePanel;
    private JFrame frame;
    private StartMenu startMenu;

    @BetterComments(description="Singleton pattern used to create GameMaster Object",type="constructor")
    public GameMaster(){
        this.isGameRunning = true;
        this.chefList = new ArrayList<>();
        this.activeChefIndex = 0;
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

    @BetterComments(description="Replace the start menu and initialize the main gameplay panel",type="method")
    public void startGame(){
        frame.getContentPane().removeAll();

        gamePanel = new GamePanel(this);
        frame.add(gamePanel);

        frame.revalidate();
        frame.repaint();
        frame.pack();

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


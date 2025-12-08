package core;

import entities.Chef;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GameMaster {
    private String currChefId;
    private Boolean isGameRunning;
    private List<Chef> chefList;
    private int activeChefIndex;
    private GamePanel gamePanel;

    public GameMaster(){
        this.isGameRunning = true;
        this.chefList = new ArrayList<>();
        this.activeChefIndex = 0;
    }

    public void main(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setTitle("NimonsCooked");

        gamePanel = new GamePanel(this);
        frame.add(gamePanel);

        frame.pack();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.startGameThread();
    }

    public void addChef(Chef chef) {
        chefList.add(chef);
        if (chefList.size() == 1) {
            currChefId = chef.getId();
        }
    }

    public Chef getActiveChef() {
        if (chefList.isEmpty()) {
            return null;
        }
        return chefList.get(activeChefIndex);
    }

    public void switchChef() {
        if (chefList.size() > 1) {
            activeChefIndex = (activeChefIndex + 1) % chefList.size();
            currChefId = chefList.get(activeChefIndex).getId();
            System.out.println("Switched to Chef " + (activeChefIndex + 1));
        }
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

    public void setCurrChefId(String Str){
        this.currChefId = Str;
    }

    public String getCurrChefId(){
        return this.currChefId;
    }




}


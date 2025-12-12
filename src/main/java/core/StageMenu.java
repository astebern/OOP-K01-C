package core;

import utils.BetterComments;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StageMenu extends JPanel {
    private JButton stage1Button;
    private JButton stage2Button;
    private JButton stage3Button;
    private JButton stage4Button;
    private JButton backButton;
    private GameMaster gameMaster;
    private BufferedImage backgroundImage;

    private final int screenWidth = 14 * 96;  // 1344
    private final int screenHeight = 10 * 96; // 960

    @BetterComments(description = "Sets up stage selection menu screen", type = "constructor")
    public StageMenu(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
        setLayout(null);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(new Color(30, 30, 30));

        loadBackgroundImage();

        int buttonWidth = 252;
        int buttonHeight = 104;
        int Y = 616;
        
        // Ambil data stage yang terbuka
        int unlocked = gameMaster.getUnlockedStages();

        // STAGE 1 (Selalu Buka)
        stage1Button = createStageButton("1", 87, Y, buttonWidth, buttonHeight, true);
        stage1Button.addActionListener(e -> selectStage(1));
        add(stage1Button);

        // STAGE 2 (Buka jika unlocked >= 2)
        stage2Button = createStageButton("2", 375, Y,buttonWidth, buttonHeight, unlocked >= 2);
        stage2Button.addActionListener(e -> selectStage(2));
        add(stage2Button);

        // STAGE 3 (Buka jika unlocked >= 3)
        stage3Button = createStageButton("3", 665, Y, buttonWidth, buttonHeight, unlocked >= 3);
        stage3Button.addActionListener(e -> selectStage(3));
        add(stage3Button);

        // STAGE 4 (Buka jika unlocked >= 4)
        stage4Button = createStageButton("4", 991, 665, buttonWidth, 60, unlocked >= 4);
        stage4Button.addActionListener(e -> selectStage(4));
        add(stage4Button);

        // BACK button
        backButton = createStageButton("", 45, 850, 183, 68, true);
        backButton.addActionListener(e -> goBack());
        add(backButton);
    }

    @BetterComments(description = "Creates transparent stage selection buttons with lock logic", type = "method")
    private JButton createStageButton(String text, int x, int y, int width, int height, boolean isUnlocked) {
        JButton button = new JButton(isUnlocked ? "" : "LOCKED"); // Teks 'LOCKED' jika terkunci
        button.setBounds(x, y, width, height);
        button.setContentAreaFilled(false);
        
        if (isUnlocked) {
            button.setBorderPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setEnabled(true);
        } else {
            // Visualisasi tombol terkunci
            button.setBorderPainted(true);
            button.setBackground(new Color(50, 50, 50, 180));
            button.setForeground(Color.RED);
            button.setOpaque(true);
            button.setEnabled(false); // Disable klik
        }
        
        button.setFocusPainted(false);
        return button;
    }

    @BetterComments(description = "Handle stage selection and start the game", type = "method")
    private void selectStage(int stageNumber) {
        System.out.println("Stage " + stageNumber + " selected!");
        gameMaster.startGame(stageNumber); // Pass stage number
    }

    @BetterComments(description = "Go back to start menu", type = "method")
    private void goBack() {
        gameMaster.showStartMenu();
    }

    @BetterComments(description = "Loads background image", type = "method")
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/stage_background.png"));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, screenWidth, screenHeight, this);
        }
    }
}
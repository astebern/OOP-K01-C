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

        // STAGE 1 button
        stage1Button = createStageButton("", 87, Y, buttonWidth, buttonHeight);
        stage1Button.addActionListener(e -> selectStage(1));
        add(stage1Button);

        // STAGE 2 button
        stage2Button = createStageButton("", 375, Y,buttonWidth, buttonHeight);
        stage2Button.addActionListener(e -> selectStage(2));
        add(stage2Button);

        // STAGE 3 button
        stage3Button = createStageButton("", 665, Y, buttonWidth, buttonHeight);
        stage3Button.addActionListener(e -> selectStage(3));
        add(stage3Button);

        // STAGE 4 button
        stage4Button = createStageButton("", 991, 665, buttonWidth, 60);
        stage4Button.addActionListener(e -> selectStage(4));
        add(stage4Button);

        // BACK button
        backButton = createStageButton("", 45, 850, 183, 68);
        backButton.addActionListener(e -> goBack());
        add(backButton);
    }

    @BetterComments(description = "Creates transparent stage selection buttons", type = "method")
    private JButton createStageButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));


        return button;
    }

    @BetterComments(description = "Handle stage selection and start the game", type = "method")
    private void selectStage(int stageNumber) {
        System.out.println("Stage " + stageNumber + " selected!");
        gameMaster.startGame();
    }

    @BetterComments(description = "Go back to start menu", type = "method")
    private void goBack() {
        gameMaster.showStartMenu();
    }

    @BetterComments(description = "Loads background image", type = "method")
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/stage_background.png"));
            System.out.println("Stage background image loaded successfully!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Stage background image not found. Using solid color background.");
            System.out.println("Place your image at: src/main/resources/stage_background.png");
            System.out.println("Recommended size: " + screenWidth + "x" + screenHeight + " pixels");
            backgroundImage = null;
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


package core;

import utils.BetterComments;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class StartMenu extends JPanel {
    private JButton startButton;
    private JButton howToPlayButton;
    private JButton exitButton;
    private GameMaster gameMaster;
    private BufferedImage backgroundImage;

    private final int screenWidth = 14 * 96;  // 1344
    private final int screenHeight = 10 * 96; // 960

    @BetterComments(description = "sets up entire start menu screen",type="constructor")
    public StartMenu(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
        setLayout(null);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(new Color(30, 30, 30));

        loadBackgroundImage();

        int buttonWidth = 555;
        int buttonHeight = 115;
        int centerX = 410;

        // START GAME button
        startButton = createTransparentButton(centerX, 223, buttonWidth, buttonHeight);
        startButton.addActionListener(e -> startGame());
        add(startButton);

        // HOW TO PLAY button
        howToPlayButton = createTransparentButton(centerX, 388, buttonWidth, buttonHeight);
        howToPlayButton.addActionListener(e -> showHowToPlay());
        add(howToPlayButton);

        // EXIT button
        exitButton = createTransparentButton(centerX, 550, buttonWidth, buttonHeight);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);
    }

    @BetterComments(description = "Creates buttons",type="method")
    private JButton createTransparentButton(int x, int y, int width, int height) {
        JButton button = new JButton();
        button.setBounds(x, y, width, height);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @BetterComments(description = "Transition from StartMenu to StageMenu" ,type="method")
    private void startGame() {
        gameMaster.showStageMenu();
    }

    @BetterComments(description = "Transition from StartMenu to HowToPlayMenu" ,type="method")
    private void showHowToPlay() {
        gameMaster.showHowToPlayMenu();
    }

    @BetterComments(description = "loads background image" , type="method")
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/menu_background.png"));
            System.out.println("Background image loaded successfully!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Background image not found. Using solid color background.");
            System.out.println("Place your image at: src/main/resources/menu_background.png");
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

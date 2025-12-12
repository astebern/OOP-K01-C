package core;

import utils.BetterComments;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class HowToPlayMenu extends JPanel {
    private JButton backButton;
    private GameMaster gameMaster;
    private BufferedImage backgroundImage;

    private final int screenWidth = 14 * 96;  // 1344
    private final int screenHeight = 10 * 96; // 960

    @BetterComments(description = "Sets up how to play menu screen with controls image", type = "constructor")
    public HowToPlayMenu(GameMaster gameMaster) {
        this.gameMaster = gameMaster;
        setLayout(null);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(new Color(30, 30, 30));

        loadBackgroundImage();

        // BACK button - same position as StageMenu's back button
        backButton = createTransparentButton("", 45, 850, 183, 68);
        backButton.addActionListener(e -> goBack());
        add(backButton);
    }

    @BetterComments(description = "Creates transparent button", type = "method")
    private JButton createTransparentButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    @BetterComments(description = "Go back to start menu", type = "method")
    private void goBack() {
        gameMaster.showStartMenu();
    }

    @BetterComments(description = "Loads controls background image", type = "method")
    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResourceAsStream("/controls_background.png"));
            System.out.println("Controls background image loaded successfully!");
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Controls background image not found. Using solid color background.");
            System.out.println("Place your image at: src/main/resources/controls_background.png");
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


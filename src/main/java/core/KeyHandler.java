package core;

import utils.Direction;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class KeyHandler implements KeyListener{
    public Direction directionNow = Direction.NONE;
    public boolean switchChef = false;
    public boolean interactPressed = false;
    public boolean pickUpDropPressed = false;
    private final Set<Integer> pressedKeys = new HashSet<>();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_TAB) {
            switchChef = true;
            System.out.println("TAB key pressed - switchChef flag set to true");
            e.consume();
            return;
        }

        if (code == KeyEvent.VK_E) {
            interactPressed = true;
            return;
        }

        if (code == KeyEvent.VK_Q) {
            pickUpDropPressed = true;
            return;
        }

        pressedKeys.add(code);
        updateDirection();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        pressedKeys.remove(code);
        updateDirection();
    }

    private void updateDirection() {
        boolean wPressed = pressedKeys.contains(KeyEvent.VK_W);
        boolean sPressed = pressedKeys.contains(KeyEvent.VK_S);
        boolean aPressed = pressedKeys.contains(KeyEvent.VK_A);
        boolean dPressed = pressedKeys.contains(KeyEvent.VK_D);

        if ((wPressed && sPressed) || (aPressed && dPressed)) {
            directionNow = Direction.NONE;
        } else if (wPressed) {
            directionNow = Direction.UP;
        } else if (sPressed) {
            directionNow = Direction.DOWN;
        } else if (aPressed) {
            directionNow = Direction.LEFT;
        } else if (dPressed) {
            directionNow = Direction.RIGHT;
        } else {
            directionNow = Direction.NONE;
        }
    }
}

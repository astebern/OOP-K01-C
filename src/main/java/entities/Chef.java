package entities;

import core.GamePanel;
import core.KeyHandler;
import items.Item;
import map.GameMap;
import utils.Actions;
import utils.BetterComments;
import utils.Direction;
import utils.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Chef {
    private String id ;
    private String name;
    private Position position;
    private Direction direction;
    private Item inventory;
    private Actions Action;
    private boolean state;
    final private int speed;
    private GamePanel gp;
    private KeyHandler keyH;
    private GameMap gameMap;
    public BufferedImage up1,up2,down1,down2,left1,left2,left3,right1,right2,right3;
    private int spriteCounter = 0;
    private int spriteNum = 1;

    @BetterComments(description = "Initializes a chef character at the given position",type="constructor")
    public Chef(GamePanel gp, KeyHandler keyH, GameMap gameMap, int x, int y){
        this.gp = gp;
        this.keyH=keyH;
        this.gameMap = gameMap;
        this.position = new Position(x, y);
        this.speed =7;
        this.direction = Direction.DOWN;
        getImage();
    }

    @BetterComments(description ="Loads the chef’s directional sprite images from the resources folder" ,type = "method")
    public void getImage(){
        try{
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/up1.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/up2.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/down1.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/down2.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/left1.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/left2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("/player/left3.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/right1.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/right2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("/player/right3.png"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @BetterComments(description = "Reads movement input, calculates the next position, checks for tile collisions",type="method")
    public void update(){
        if (keyH.directionNow != null && keyH.directionNow != Direction.NONE) {
            int nextX = this.position.getX();
            int nextY = this.position.getY();

            if (keyH.directionNow==Direction.UP){
                nextY = this.position.getY()-speed;
                this.direction=Direction.UP;
            }
            else if (keyH.directionNow==Direction.DOWN){
                nextY = this.position.getY()+speed;
                this.direction=Direction.DOWN;
            }
            else if (keyH.directionNow==Direction.LEFT){
                nextX = this.position.getX()-speed;
                this.direction=Direction.LEFT;
            }
            else if (keyH.directionNow==Direction.RIGHT){
                nextX = this.position.getX()+speed;
                this.direction=Direction.RIGHT;
            }

            boolean collision = false;
            int solidAreaSize = gp.tileSize - 20;
            int solidAreaOffset = 10;


            if (gameMap.checkCollision(nextX + solidAreaOffset, nextY + solidAreaOffset) ||
                gameMap.checkCollision(nextX + solidAreaOffset + solidAreaSize, nextY + solidAreaOffset) ||
                gameMap.checkCollision(nextX + solidAreaOffset, nextY + solidAreaOffset + solidAreaSize) ||
                gameMap.checkCollision(nextX + solidAreaOffset + solidAreaSize, nextY + solidAreaOffset + solidAreaSize)) {
                collision = true;
            }


            if (!collision) {
                this.position.setX(nextX);
                this.position.setY(nextY);
            }


            spriteCounter++;
            if (spriteCounter > 10) {
                spriteCounter = 0;
                if (direction == Direction.UP || direction == Direction.DOWN) {
                    spriteNum = spriteNum == 1 ? 2 : 1;
                } else if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                    spriteNum++;
                    if (spriteNum > 3) spriteNum = 1;
                }
            }
        }
    }

    @BetterComments(description = "Renders the current animation frame of the chef at its position on the screen",type="method")
    public void draw(Graphics2D g2){
        BufferedImage image = null;
        switch (direction){
            case UP:
                image = spriteNum == 1 ? up1 : up2;
                break;
            case DOWN:
                image = spriteNum == 1 ? down1 : down2;
                break;
            case LEFT:
                if (spriteNum == 1) image = left1;
                else if (spriteNum == 2) image = left3;
                else if (spriteNum == 3) image = left2;
                break;
            case RIGHT:
                if (spriteNum == 1) image = right1;
                else if (spriteNum == 2) image = right3;
                else if (spriteNum == 3) image = right2;
                break;
        }
        g2.drawImage(image,this.position.getX(),this.position.getY(),gp.tileSize,gp.tileSize,null);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Item getInventory() {
        return inventory;
    }

    public void setInventory(Item inventory) {
        this.inventory = inventory;
    }

    public Actions getAction() {
        return Action;
    }

    public void setAction(Actions action) {
        this.Action = action;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getSpeed() {
        return speed;
    }

    public GamePanel getGp() {
        return gp;
    }

    public KeyHandler getKeyH() {
        return keyH;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public int getSpriteCounter() {
        return spriteCounter;
    }

    public void setSpriteCounter(int spriteCounter) {
        this.spriteCounter = spriteCounter;
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public void setSpriteNum(int spriteNum) {
        this.spriteNum = spriteNum;
    }

}


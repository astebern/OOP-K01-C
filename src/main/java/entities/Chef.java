package entities;

import core.GamePanel;
import core.KeyHandler;
import items.Item;
import map.GameMap;
import stations.Station;
import utils.Actions;
import utils.BetterComments;
import utils.Direction;
import utils.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Chef {
    private static int chef_num=1;
    private String id ;
    private String name;
    private Position position;
    private Direction direction;
    private Item inventory;
    private Actions currentActions;
    private boolean state; //true=not busy , false =busy
    final private int speed;
    private GamePanel gp;
    private KeyHandler keyH;
    private GameMap gameMap;
    public BufferedImage up1,up2,down1,down2,left1,left2,left3,right1,right2,right3;
    private int spriteCounter = 0;
    private int spriteNum = 1;

    @BetterComments(description = "Initializes a chef character at the given position",type="constructor")
    public Chef(GamePanel gp, KeyHandler keyH, GameMap gameMap, int x, int y) {
        // TODO: Name
        this.inventory = null;
        this.currentActions = Actions.IDLE;
        this.state = true;
        this.id = "Chef" + chef_num;
        this.gp = gp;
        this.keyH = keyH;
        this.gameMap = gameMap;
        this.position = new Position(x, y);
        this.speed = 7;
        this.direction = Direction.DOWN;
        getImage();
        chef_num++;
    }

    @BetterComments(description = "Loads the chef's directional sprite images from the resources folder", type = "method")
    public void getImage() {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BetterComments(description = "Reads movement input, calculates the next position, checks for tile collisions",type="method")
    public void update(){
        if (keyH.pickUpDropPressed) {
            pickUpDrop();
            keyH.pickUpDropPressed = false;
            return;
        }

        if (keyH.interactPressed) {
            interactWithStation();
            keyH.interactPressed = false;
            return;
        }

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
                this.currentActions = Actions.MOVING;
                this.state = true;
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
        } else {
            if (this.currentActions == Actions.MOVING) {
                this.currentActions = Actions.IDLE;
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

        if (inventory != null && inventory.getImage() != null) {
            int itemSize = gp.tileSize / 2;
            int itemX = this.position.getX() + (gp.tileSize - itemSize) / 2;
            int itemY = this.position.getY() - itemSize / 2;
            g2.drawImage(inventory.getImage(), itemX, itemY, itemSize, itemSize, null);
        }
    }

    @BetterComments(description = "Handles picking up items from stations or dropping items from inventory", type="method")
    public void pickUpDrop(){
        // Get the target tile in front of the player (follows the green indicator)
        int targetX = getTileX();
        int targetY = getTileY();

        switch (direction) {
            case UP:
                targetY--;
                break;
            case DOWN:
                targetY++;
                break;
            case LEFT:
                targetX--;
                break;
            case RIGHT:
                targetX++;
                break;
        }

        // Only check bounds
        if (targetX < 0 || targetX >= gameMap.getMapWidth() ||
            targetY < 0 || targetY >= gameMap.getMapHeight()) {
            return;
        }


        if (inventory == null) {
            // PICKING UP
            if (!gameMap.canHoldItem(targetX, targetY)) {
                System.out.println(id + " cannot pickup items from this tile type");
                return;
            }

            Item item = gameMap.getItemAt(targetX, targetY);
            if (item != null) {
                // Check if item is portable
                if (!item.isPortable()) {
                    System.out.println(id + " cannot pick up " + item.getClass().getSimpleName() +
                                     " - item is not portable");
                    return;
                }

                this.inventory = gameMap.removeItemAt(targetX, targetY);
                this.currentActions = Actions.PICKINGUP;
                this.state = true;
                System.out.println(id + " picked up " + item.getClass().getSimpleName() + 
                                 " from (" + targetX + ", " + targetY + ")");
            } else {
                System.out.println(id + " - no item to pick up at (" + targetX + ", " + targetY + ")");
            }
        } else {
            // DROPPING - can drop anywhere the indicator shows (within bounds)
            Item existingItem = gameMap.getItemAt(targetX, targetY);
            if (existingItem == null) {
                boolean success = gameMap.placeItemAt(targetX, targetY, this.inventory);
                if (success) {
                    System.out.println(id + " dropped " + this.inventory.getClass().getSimpleName() +
                                     " at (" + targetX + ", " + targetY + ")");
                    this.inventory = null;
                    this.currentActions = Actions.DROPPINGDOWN;
                    this.state = true;
                } else {
                    System.out.println(id + " failed to drop item");
                }
            } else {
                System.out.println(id + " cannot drop - tile already has an item");
            }
        }

        this.currentActions = Actions.IDLE;
    }

    @BetterComments(description = "Handles interaction with stations to use their functionality", type="method")
    public void interactWithStation(){
        int targetX = getTileX();
        int targetY = getTileY();

        switch (direction) {
            case UP:
                targetY--;
                break;
            case DOWN:
                targetY++;
                break;
            case LEFT:
                targetX--;
                break;
            case RIGHT:
                targetX++;
                break;
        }

        if (targetX < 0 || targetX >= gameMap.getMapWidth() ||
            targetY < 0 || targetY >= gameMap.getMapHeight()) {
            return;
        }

        // Check distance for station interaction
        int chefCenterX = this.position.getX() + gp.tileSize / 2;
        int chefCenterY = this.position.getY() + gp.tileSize / 2;
        int targetCenterX = targetX * gp.tileSize + gp.tileSize / 2;
        int targetCenterY = targetY * gp.tileSize + gp.tileSize / 2;

        double distance = Math.sqrt(Math.pow(chefCenterX - targetCenterX, 2) + Math.pow(chefCenterY - targetCenterY, 2));
        double maxDistance = gp.tileSize;

        if (distance > maxDistance) {
            System.out.println(id + " is too far from the station");
            return;
        }

        Station station = gameMap.getStationAt(targetX, targetY);

        if (station != null) {
            this.currentActions = Actions.USINGSTATION;
            this.state = false;
            System.out.println(id + " is interacting with " + station.getClass().getSimpleName() +
                             " at (" + targetX + ", " + targetY + ")");

            // TODO:Implement stuff per station
            // nanti gw buat MALAS SKRG
            // After using station
            this.state = true;
            this.currentActions = Actions.IDLE;
        } else {
            System.out.println(id + " - no station to interact with");
        }
    }

    @BetterComments(description = "Gets the tile X coordinate the chef is standing on", type="method")
    private int getTileX() {
        return (this.position.getX() + gp.tileSize / 2) / gp.tileSize;
    }

    @BetterComments(description = "Gets the tile Y coordinate the chef is standing on", type="method")
    private int getTileY() {
        return (this.position.getY() + gp.tileSize / 2) / gp.tileSize;
    }

    @BetterComments(description = "Gets the tile X coordinate in front of the chef based on direction", type="method")
    public int getTargetTileX() {
        int targetX = getTileX();
        if (direction == Direction.LEFT) {
            targetX--;
        } else if (direction == Direction.RIGHT) {
            targetX++;
        }
        return targetX;
    }

    @BetterComments(description = "Gets the tile Y coordinate in front of the chef based on direction", type="method")
    public int getTargetTileY() {
        int targetY = getTileY();
        if (direction == Direction.UP) {
            targetY--;
        } else if (direction == Direction.DOWN) {
            targetY++;
        }
        return targetY;
    }

    // Getters and Setters
    public String getId() {
        return id;
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

    public Actions getCurrentActions() {
        return currentActions;
    }

    public void setCurrentActions(Actions currentActions) {
        this.currentActions = currentActions;
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


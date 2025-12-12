package entities;

import core.GamePanel;
import core.KeyHandler;
import items.Item;
import map.GameMap;
import stations.CookingStation;
import stations.CuttingStation;
import stations.PlateStorage;
import stations.Station;
import stations.TrashStation;
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

    // Station interaction tracking
    private Station activeStation = null;
    private int activeStationX = -1;
    private int activeStationY = -1;

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
            // Pause any active station when chef starts moving
            if (activeStation != null && activeStation.isInProgress()) {
                activeStation.pause();
                System.out.println(id + " moved away, pausing station");
                // Reset chef state when moving away
                this.state = true;
                this.currentActions = Actions.IDLE;
            }
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

            // If inventory is a plate with contents, draw the ingredient on top
            if (inventory instanceof items.equipment.Plate) {
                items.equipment.Plate plate = (items.equipment.Plate) inventory;
                if (!plate.getContents().isEmpty()) {
                    items.Preparable content = plate.getContents().get(0);
                    if (content instanceof Item) {
                        Item ingredientItem = (Item) content;
                        if (ingredientItem.getImage() != null) {
                            // Draw ingredient on top of plate (smaller)
                            int ingredientSize = gp.tileSize / 3;
                            int ingredientX = this.position.getX() + (gp.tileSize - ingredientSize) / 2;
                            int ingredientY = this.position.getY() - itemSize / 2 - 3; // Slightly above plate
                            g2.drawImage(ingredientItem.getImage(), ingredientX, ingredientY, ingredientSize, ingredientSize, null);
                        }
                    }
                }
            }
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

            // Check if picking up from PlateStorage
            Station stationAtTile = gameMap.getStationAt(targetX, targetY);
            if (stationAtTile instanceof PlateStorage) {
                PlateStorage plateStorage = (PlateStorage) stationAtTile;
                if (plateStorage.hasPlates()) {
                    items.equipment.Plate plate = plateStorage.takePlate();
                    if (plate != null) {
                        this.inventory = plate;
                        this.currentActions = Actions.PICKINGUP;
                        this.state = true;
                        System.out.println(id + " picked up plate from PlateStorage (" +
                                         plateStorage.getPlateCount() + " plates remaining)");
                        return;
                    }
                } else {
                    System.out.println(id + " - PlateStorage is empty");
                    return;
                }
            }

            if (!gameMap.canHoldItem(targetX, targetY)) {
                System.out.println(id + " cannot pickup items from this tile type");
                return;
            }

            Item item = gameMap.getItemAt(targetX, targetY);
            if (item != null) {
                // Special case: If item is a cooking device (not a Plate) with contents, pick up the ingredient inside
                if (item instanceof items.equipment.CookingDevice &&
                    item instanceof items.equipment.KitchenUtensil) {
                    items.equipment.KitchenUtensil utensil = (items.equipment.KitchenUtensil) item;

                    if (!utensil.getContents().isEmpty()) {
                        // Pick up ingredient from inside the utensil
                        items.Preparable ingredientInside = utensil.getContents().get(0);

                        // Stop cooking if it's on a cooking station
                        // Reuse stationAtTile variable already declared at the start of pickup block
                        if (stationAtTile instanceof CookingStation && stationAtTile.isInProgress()) {
                            ((CookingStation) stationAtTile).stopCooking();
                            System.out.println(id + " picked up item from utensil - cooking stopped");
                        }

                        // If ingredient is still in COOKING state (picked up before 12 seconds),
                        // revert it back to CHOPPED (or RAW for pasta) so it can be cooked again
                        if (ingredientInside instanceof items.food.Ingredient) {
                            items.food.Ingredient ingredient = (items.food.Ingredient) ingredientInside;
                            if (ingredient.getState() == items.food.IngredientState.COOKING) {
                                // For pasta (boiled), revert to RAW. For fried items, revert to CHOPPED
                                if (ingredient.getName().equalsIgnoreCase("Pasta")) {
                                    ingredient.setState(items.food.IngredientState.RAW);
                                    System.out.println(id + " - " + ingredient.getName() +
                                                     " reverted to RAW (not fully cooked)");
                                } else {
                                    ingredient.setState(items.food.IngredientState.CHOPPED);
                                    System.out.println(id + " - " + ingredient.getName() +
                                                     " reverted to CHOPPED (not fully cooked)");
                                }
                            }
                        }

                        utensil.getContents().remove(ingredientInside);
                        utensil.updateCookingImage(); // Reset to default utensil image
                        this.inventory = (Item) ingredientInside;
                        this.currentActions = Actions.PICKINGUP;
                        this.state = true;
                        System.out.println(id + " picked up " +
                                         ((items.food.Ingredient)ingredientInside).getName() +
                                         " from " + item.getClass().getSimpleName());
                        return;
                    } else {
                        System.out.println(id + " - " + item.getClass().getSimpleName() + " is empty");
                        return;
                    }
                }

                // Normal item pickup
                // Check if item is portable
                if (!item.isPortable()) {
                    System.out.println(id + " cannot pick up " + item.getClass().getSimpleName() +
                                     " - item is not portable");
                    return;
                }

                // Check if picking up from a station with progress (like CuttingStation)
                // Reuse stationAtTile variable already declared at the start of pickup block
                if (stationAtTile != null) {
                    // Stop station processes
                    if (stationAtTile instanceof CuttingStation && stationAtTile.isInProgress()) {
                        ((CuttingStation) stationAtTile).stopCutting();
                        System.out.println(id + " picked up item from station - cutting progress reset");
                    }
                    // Clear active station tracking if this was the active station
                    if (activeStation == stationAtTile) {
                        activeStation = null;
                        activeStationX = -1;
                        activeStationY = -1;
                    }
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

            // Check if dropping on a TrashStation
            Station stationAtTile = gameMap.getStationAt(targetX, targetY);
            if (stationAtTile instanceof TrashStation) {
                TrashStation trashStation = (TrashStation) stationAtTile;
                boolean shouldDelete = trashStation.disposeItem(this.inventory);

                if (shouldDelete) {
                    // Item is deleted (ingredient, utensil, etc.)
                    this.inventory = null;
                    System.out.println(id + " disposed item in trash");
                } else {
                    // Item remains in inventory (plate with cleared contents)
                    System.out.println(id + " cleared plate contents in trash (plate kept)");
                }

                this.currentActions = Actions.DROPPINGDOWN;
                this.state = true;
                return; // Exit early, item handled by trash
            }

            // Check if dropping on a PlateStorage
            if (stationAtTile instanceof PlateStorage) {
                PlateStorage plateStorage = (PlateStorage) stationAtTile;

                // Only allow plates to be dropped on PlateStorage
                if (this.inventory instanceof items.equipment.Plate) {
                    items.equipment.Plate plate = (items.equipment.Plate) this.inventory;

                    if (plateStorage.storePlate(plate)) {
                        this.inventory = null;
                        System.out.println(id + " returned plate to PlateStorage (" +
                                         plateStorage.getPlateCount() + " plates now in storage)");
                        this.currentActions = Actions.DROPPINGDOWN;
                        this.state = true;
                        return;
                    }
                } else {
                    System.out.println(id + " - Only plates can be placed on PlateStorage");
                    return;
                }
            }

            Item existingItem = gameMap.getItemAt(targetX, targetY);

            // Special case: If tile has a plate, try to add ingredient onto it
            if (existingItem instanceof items.equipment.Plate) {
                items.equipment.Plate plate = (items.equipment.Plate) existingItem;
                
                // Check if inventory is a preparable item (ingredient)
                if (this.inventory instanceof items.Preparable) {
                    items.Preparable preparable = (items.Preparable) this.inventory;
                    
                    // Check if plate is full (capacity: 1)
                    if (!plate.getContents().isEmpty()) {
                        System.out.println(id + " - Plate is full (capacity: 1 item)");
                        return;
                    }
                    
                    // Add ingredient to plate
                    plate.addContent(preparable);
                    System.out.println(id + " added " +
                                     ((items.food.Ingredient)preparable).getName() +
                                     " to plate");
                    this.inventory = null;
                    this.currentActions = Actions.DROPPINGDOWN;
                    this.state = true;
                    return;
                } else {
                    System.out.println(id + " - Can only add ingredients to plates");
                    return;
                }
            }

            // Special case: If tile has a cooking utensil, try to add ingredient into it
            if (existingItem != null && existingItem instanceof items.equipment.CookingDevice) {
                items.equipment.CookingDevice device = (items.equipment.CookingDevice) existingItem;
                items.equipment.KitchenUtensil utensil = (items.equipment.KitchenUtensil) existingItem;

                // Check if inventory is a preparable item
                if (this.inventory instanceof items.Preparable) {
                    items.Preparable preparable = (items.Preparable) this.inventory;

                    // Check if utensil is full
                    if (!utensil.getContents().isEmpty()) {
                        System.out.println(id + " - " + existingItem.getClass().getSimpleName() +
                                         " is already full (capacity: 1)");
                    } else if (device.canAccept(preparable)) {
                        // Add ingredient to utensil
                        device.addIngredient(preparable);
                        System.out.println(id + " added " +
                                         ((items.food.Ingredient)preparable).getName() +
                                         " to " + existingItem.getClass().getSimpleName());
                        this.inventory = null;
                        this.currentActions = Actions.DROPPINGDOWN;
                        this.state = true;
                    } else {
                        System.out.println(id + " - " + existingItem.getClass().getSimpleName() +
                                         " cannot accept this ingredient");
                    }
                } else {
                    System.out.println(id + " - Cannot add this item to cooking utensil");
                }
            }
            // Normal drop - tile is empty
            else if (existingItem == null) {
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
            }
            // Tile has non-utensil item
            else {
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
            System.out.println(id + " is interacting with " + station.getClass().getSimpleName() +
                             " at (" + targetX + ", " + targetY + ")");

            // Handle CuttingStation
            if (station instanceof CuttingStation) {
                CuttingStation cuttingStation = (CuttingStation) station;
                Item itemOnStation = gameMap.getItemAt(targetX, targetY);

                if (itemOnStation != null && cuttingStation.hasChoppableIngredient(itemOnStation)) {
                    // Set chef to busy state
                    this.currentActions = Actions.USINGSTATION;
                    this.state = false;

                    // Track this station as active
                    this.activeStation = cuttingStation;
                    this.activeStationX = targetX;
                    this.activeStationY = targetY;

                    // Start or resume cutting
                    boolean started = cuttingStation.startCutting(itemOnStation, () -> {
                        // On completion callback
                        this.state = true;
                        this.currentActions = Actions.IDLE;
                        this.activeStation = null;
                        this.activeStationX = -1;
                        this.activeStationY = -1;
                        System.out.println(id + " finished cutting");
                    });

                    if (!started) {
                        // Failed to start, reset chef state
                        this.state = true;
                        this.currentActions = Actions.IDLE;
                        this.activeStation = null;
                        this.activeStationX = -1;
                        this.activeStationY = -1;
                    }
                } else if (itemOnStation == null) {
                    System.out.println(id + " - No item on cutting station to cut");
                } else {
                    System.out.println(id + " - Item on station cannot be chopped");
                }
            }
            // Handle CookingStation
            else if (station instanceof CookingStation) {
                CookingStation cookingStation = (CookingStation) station;
                Item itemOnStation = gameMap.getItemAt(targetX, targetY);

                // Case 1: Chef has ingredient in inventory, and there's a utensil on station
                if (this.inventory != null && itemOnStation != null &&
                    itemOnStation instanceof items.equipment.CookingDevice) {

                    items.equipment.KitchenUtensil utensil = (items.equipment.KitchenUtensil) itemOnStation;

                    // Try to add ingredient to utensil
                    if (this.inventory instanceof items.Preparable) {
                        items.Preparable preparable = (items.Preparable) this.inventory;

                        if (utensil.getContents().size() >= 1) {
                            System.out.println(id + " - " + itemOnStation.getClass().getSimpleName() +
                                             " is already full (capacity: 1)");
                        } else {
                            items.equipment.CookingDevice device = (items.equipment.CookingDevice) itemOnStation;

                            if (device.canAccept(preparable)) {
                                device.addIngredient(preparable);
                                System.out.println(id + " added " +
                                                 ((items.food.Ingredient)preparable).getName() +
                                                 " to " + itemOnStation.getClass().getSimpleName());
                                this.inventory = null; // Remove from chef's inventory
                                this.currentActions = Actions.IDLE;
                                this.state = true;
                            } else {
                                System.out.println(id + " - " + itemOnStation.getClass().getSimpleName() +
                                                 " cannot accept this ingredient");
                            }
                        }
                    } else {
                        System.out.println(id + " - Cannot add this item to cooking utensil");
                    }
                }
                // Case 2: No ingredient in hand, utensil has ingredient - start cooking
                else if (this.inventory == null && itemOnStation instanceof items.equipment.CookingDevice) {
                    // Check if utensil has cookable content
                    items.equipment.KitchenUtensil utensil = (items.equipment.KitchenUtensil) itemOnStation;
                    if (!utensil.getContents().isEmpty() &&
                        utensil.getContents().get(0) instanceof items.Preparable) {

                        items.Preparable prep = utensil.getContents().get(0);
                        if (prep.canBeCooked()) {
                            // Suppress false positive from IntelliJ - this compiles fine in Maven
                            @SuppressWarnings("unchecked")
                            boolean started = cookingStation.startCooking(itemOnStation);

                            if (started) {
                                System.out.println(id + " started cooking");
                                this.currentActions = Actions.IDLE;
                                this.state = true;
                            } else {
                                System.out.println(id + " failed to start cooking");
                            }
                        } else {
                            System.out.println(id + " - Item in utensil cannot be cooked");
                        }
                    } else {
                        System.out.println(id + " - Utensil is empty");
                    }
                }
                // Case 3: No utensil on station
                else if (itemOnStation == null) {
                    System.out.println(id + " - No cooking utensil on station (place pot/pan first)");
                }
                // Case 4: Utensil is empty
                else if (itemOnStation instanceof items.equipment.CookingDevice) {
                    items.equipment.KitchenUtensil utensil = (items.equipment.KitchenUtensil) itemOnStation;
                    if (utensil.getContents().isEmpty()) {
                        System.out.println(id + " - " + itemOnStation.getClass().getSimpleName() +
                                         " is empty (hold ingredient and interact to add)");
                    } else {
                        System.out.println(id + " - Cannot start cooking (check ingredient state)");
                    }
                } else {
                    System.out.println(id + " - Item on station is not a cooking utensil");
                }
            }
            // TODO: Add other station types here
            else {
                // Default behavior for other stations
                this.currentActions = Actions.USINGSTATION;
                this.state = false;
                // After using station
                this.state = true;
                this.currentActions = Actions.IDLE;
            }
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


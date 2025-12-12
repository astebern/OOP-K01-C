package map;

import core.GamePanel;
import items.Item;
import items.equipment.BoilingPot;
import items.equipment.FryingPan;
import items.equipment.Plate;
import items.food.Ingredient;
import stations.*;
import utils.BetterComments;
import utils.Position;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameMap {
    GamePanel gp;
    Tile[] tiles;
    int[][] mapData;
    private final Map<Position, TileData> tileDataMap;
    private static final int MAP_WIDTH = 14;
    private static final int MAP_HEIGHT = 10;

    @BetterComments(description = "inner class to store Station (for station tiles) AND single Item (all tiles except walls can hold items)",type="inner class")
    private static class TileData {
        Station station;
        Item item;

        public TileData(Station station, Item item) {
            this.station = station;
            this.item = item;
        }
    }

    @BetterComments(description = "Creates the tile array and map grid, loads tile graphics and collision settings, and initializes the kitchen layout",type="constructor")
    public GameMap(GamePanel gp){
        this.gp = gp;
        tiles = new Tile [11];
        mapData = new int[MAP_HEIGHT][MAP_WIDTH];
        tileDataMap = new HashMap<>();
        getTileImage();
        initializeMap();
    }

    @BetterComments(description ="Populates Map Data" ,type="method")
    private void initializeMap() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                mapData[y][x] = 0;
            }
        }

        mapData[0][0] = 8;
        mapData[1][0] = 5;
        mapData[2][0] = 5;
        mapData[3][0] = 5;
        mapData[4][0] = 8;
        mapData[5][0] = 4;
        mapData[6][0] = 7;
        mapData[7][0] = 7;
        mapData[8][0] = 8;
        mapData[9][0] = 2;

        mapData[0][1] = 8;
        mapData[9][1] = 2;

        mapData[0][2] = 9;
        mapData[3][2] = 1;
        mapData[9][2] = 2;

        mapData[0][3] = 9;
        mapData[9][3] = 2;

        mapData[0][4] = 8;
        mapData[9][4] = 2;

        mapData[0][5] = 8;
        mapData[1][5] = 8;
        mapData[2][5] = 8;
        mapData[3][5] = 8;
        mapData[4][5] = 2;
        mapData[5][5] = 2;
        mapData[6][5] = 2;
        mapData[7][5] = 2;
        mapData[9][5] = 2;

        mapData[0][6] = 2;
        mapData[1][6] = 2;
        mapData[2][6] = 2;
        mapData[3][6] = 2;
        mapData[4][6] = 2;
        mapData[5][6] = 2;
        mapData[6][6] = 2;
        mapData[7][6] = 2;
        mapData[9][6] = 2;

        mapData[0][7] = 2;
        mapData[1][7] = 2;
        mapData[2][7] = 2;
        mapData[3][7] = 2;
        mapData[4][7] = 2;
        mapData[5][7] = 2;
        mapData[6][7] = 2;
        mapData[7][7] = 2;
        mapData[9][7] = 2;

        mapData[0][8] = 2;
        mapData[1][8] = 2;
        mapData[2][8] = 2;
        mapData[3][8] = 2;
        mapData[4][8] = 2;
        mapData[5][8] = 2;
        mapData[6][8] = 10;
        mapData[7][8] = 8;
        mapData[9][8] = 2;

        mapData[0][9] = 2;
        mapData[9][9] = 2;

        mapData[0][10] =2;
        mapData[9][10] =2;

        mapData[0][11] =2;
        mapData[6][11] =1;
        mapData[9][11] =2;

        mapData[0][12] =2;
        mapData[9][12] =2;

        mapData[0][13] =2;
        mapData[1][13] =6;
        mapData[2][13] =6;
        mapData[3][13] =8;
        mapData[4][13] =9;
        mapData[5][13] =9;
        mapData[6][13] =5;
        mapData[7][13] =5;
        mapData[8][13] =3;
        mapData[9][13] =2;

        // Populate tile data map with stations (no items)
        populateTileData();

    }

    @BetterComments(description = "Populates the tile data map with station instances for station tiles and item slot for floor/spawn tiles only", type="method")
    private void populateTileData() {
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                int tileId = mapData[y][x];

                if (tileId == 2) {
                    // Wall tiles - skip
                    continue;
                } else if (tileId == 0 || tileId == 1) {
                    // Floor and spawn tiles
                    setTileData(x, y, null, null);
                } else if (tileId == 5) {
                    Station station = createStationForTile(tileId);
                    Ingredient ingredient = null;

                    if (x == 0 && y == 1) {
                        ingredient = Ingredient.create("Ikan");
                        ingredient.setChoppable(true);
                        ingredient.setCookable(true);
                        ingredient.setCanBePlacedOnPlate(true);

                    } else if (x == 0 && y == 2) {
                        ingredient = Ingredient.create("Daging");
                        ingredient.setChoppable(true);
                        ingredient.setCookable(true);
                        ingredient.setCanBePlacedOnPlate(true);
                    } else if (x == 0 && y == 3) {
                        ingredient = Ingredient.create("Udang");
                        ingredient.setChoppable(true);
                        ingredient.setCookable(true);
                        ingredient.setCanBePlacedOnPlate(true);
                    } else if (x == 13 && y == 6) {
                        ingredient = Ingredient.create("Pasta");
                        ingredient.setChoppable(false);
                        ingredient.setCookable(true);
                        ingredient.setCanBePlacedOnPlate(true);
                    } else if (x == 13 && y == 7) {
                        ingredient = Ingredient.create("Tomat");
                        ingredient.setChoppable(true);
                        ingredient.setCookable(true);
                        ingredient.setCanBePlacedOnPlate(true);
                    }

                    setTileData(x, y, station, ingredient);
                } else if (tileId == 4) {
                    // Plate Storage - add plate
                    Station station = createStationForTile(tileId);
                    Plate plate = new Plate();
                    System.out.println("Placed Plate at PlateStorage (" + x + ", " + y + ")");
                    setTileData(x, y, station, plate);
                } else if (tileId == 9) {
                    // Cooking Station - add utensil based on x position
                    Station station = createStationForTile(tileId);
                    Item utensil = null;
                    
                    // Cooking stations at x=2 or x=3 get BoilingPot, stations at x=13 get FryingPan
                    if (x == 2 || x == 3) {
                        utensil = new BoilingPot();
                        System.out.println("Placed BoilingPot at CookingStation (" + x + ", " + y + ")");
                    } else if (x == 13) {
                        utensil = new FryingPan();
                        System.out.println("Placed FryingPan at CookingStation (" + x + ", " + y + ")");
                    }
                    
                    setTileData(x, y, station, utensil);
                } else {
                    // Other station tiles
                    Station station = createStationForTile(tileId);
                    if (station != null) {
                        setTileData(x, y, station, null);
                    }
                }
            }
        }
    }

    @BetterComments(description = "Creates the appropriate station instance based on tile ID", type="method")
    private Station createStationForTile(int tileId) {
        switch (tileId) {
            case 3:  // Trash Station
                return new TrashStation();
            case 4:  // Plate Storage
                return new PlateStorage();
            case 5:  // Ingredient Storage
                return new IngredientStorage();
            case 6:  // Washing Station
                return new WashingStation();
            case 7:  // Serving Counter
                return new ServingCounter();
            case 8:  // Assembly Station
                return new AssemblyStation();
            case 9:  // Cooking Station
                return new CookingStation();
            case 10: // Cutting Station
                return new CuttingStation();
            default:
                return null; // No station for floor, spawn, and wall tiles
        }
    }

    @BetterComments(description = "Loads all tile images from resources and configures which tiles are walkable or solid",type="method")
    public void getTileImage() {
        try {
            // Floor (walkable space)
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/tile/floor_tile.png"));
            tiles[0].collision = false;
            // Spawn point
            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/tile/spawn_tile.png"));
            tiles[1].collision = false;
            // Wall
            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/tile/wall_tile.png"));
            tiles[2].collision = true;
            // Trash Station
            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(getClass().getResourceAsStream("/tile/trash_tile.png"));
            tiles[3].collision = true;
            // Plate Storage
            tiles[4] = new Tile();
            tiles[4].image = ImageIO.read(getClass().getResourceAsStream("/tile/plate_tile.png"));
            tiles[4].collision = true;
            // Ingredient Storage
            tiles[5] = new Tile();
            tiles[5].image = ImageIO.read(getClass().getResourceAsStream("/tile/ingredient_tile.png"));
            tiles[5].collision = true;
            // Washing Station
            tiles[6] = new Tile();
            tiles[6].image = ImageIO.read(getClass().getResourceAsStream("/tile/washing_tile.png"));
            tiles[6].collision = true;
            // Serving Counter
            tiles[7] = new Tile();
            tiles[7].image = ImageIO.read(getClass().getResourceAsStream("/tile/serving_tile.png"));
            tiles[7].collision = true;
            // Assembly Station
            tiles[8] = new Tile();
            tiles[8].image = ImageIO.read(getClass().getResourceAsStream("/tile/assembly_tile.png"));
            tiles[8].collision = true;
            // Cooking Station
            tiles[9] = new Tile();
            tiles[9].image = ImageIO.read(getClass().getResourceAsStream("/tile/cooking_tile.png"));
            tiles[9].collision = true;
            //Cutting Station
            tiles[10] = new Tile();
            tiles[10].image = ImageIO.read(getClass().getResourceAsStream("/tile/cutting_tile.png"));
            tiles[10].collision = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BetterComments(description = "Renders the entire tile map to the screen by drawing the correct tile image at each grid position", type = "method")
    public void draw(Graphics2D g2) {
        // Draw tiles first
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                int tileId = mapData[y][x];
                int screenX = x * gp.tileSize;
                int screenY = y * gp.tileSize;
                g2.drawImage(tiles[tileId].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            }
        }

        // Draw items on top of tiles (centered, size depends on item type)
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Item item = getItemAt(x, y);
                if (item != null && item.getImage() != null) {
                    // Kitchen utensils have different sizes
                    int itemSize;
                    if (item instanceof BoilingPot) {
                        itemSize = 80; // 80x80 pixels for boiling pot
                    } else if (item instanceof FryingPan) {
                        itemSize = 64; // 64x64 pixels for frying pan
                    } else if (item instanceof Plate) {
                        itemSize = 48; // 48x48 pixels for plate
                    } else {
                        itemSize = gp.tileSize / 2; // Half tile size for ingredients
                    }
                    int screenX = x * gp.tileSize + (gp.tileSize - itemSize) / 2;
                    int screenY = y * gp.tileSize + (gp.tileSize - itemSize) / 2;
                    g2.drawImage(item.getImage(), screenX, screenY, itemSize, itemSize, null);
                }
            }
        }

        // Draw progress bars for stations that are in progress
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Station station = getStationAt(x, y);
                if (station != null && station.isInProgress()) {
                    float progress = station.getProgressPercent();

                    // Progress bar dimensions
                    int barWidth = gp.tileSize - 20;
                    int barHeight = 8;
                    int screenX = x * gp.tileSize + 10;
                    int screenY = y * gp.tileSize + gp.tileSize - 15;

                    // Draw background (dark gray)
                    g2.setColor(new Color(50, 50, 50, 200));
                    g2.fillRect(screenX, screenY, barWidth, barHeight);

                    // Draw progress (green)
                    g2.setColor(new Color(50, 200, 50, 220));
                    int fillWidth = (int) ((barWidth * progress) / 100.0f);
                    g2.fillRect(screenX, screenY, fillWidth, barHeight);

                    // Draw border (white)
                    g2.setColor(Color.WHITE);
                    g2.drawRect(screenX, screenY, barWidth, barHeight);

                    // Draw percentage text
                    g2.setFont(new Font("Arial", Font.BOLD, 10));
                    String progressText = String.format("%.0f%%", progress);
                    int textWidth = g2.getFontMetrics().stringWidth(progressText);
                    g2.setColor(Color.WHITE);
                    g2.drawString(progressText, screenX + (barWidth - textWidth) / 2, screenY - 2);
                }
            }
        }
    }

    @BetterComments(description = "returns whether that location is blocked by a collidable tile or out-of-bounds." ,type="method")
    public boolean checkCollision(int worldX, int worldY) {
        int col = worldX / gp.tileSize;
        int row = worldY / gp.tileSize;

        if (col < 0 || col >= MAP_WIDTH || row < 0 || row >= MAP_HEIGHT) {
            return true;
        }

        int tileId = mapData[row][col];
        return tiles[tileId].collision;
    }

    public int getMapWidth() {
        return MAP_WIDTH;
    }

    public int getMapHeight() {
        return MAP_HEIGHT;
    }


    @BetterComments(description = "Sets the Station and Item data for a specific tile position", type="method")
    public void setTileData(int x, int y, Station station, Item item) {
        Position pos = new Position(x, y);
        tileDataMap.put(pos, new TileData(station, item));
    }

    @BetterComments(description = "Gets the Station at a specific tile position", type="method")
    public Station getStationAt(int x, int y) {
        TileData data = tileDataMap.get(new Position(x, y));
        return data != null ? data.station : null;
    }

    @BetterComments(description = "Gets the Item at a specific tile position", type="method")
    public Item getItemAt(int x, int y) {
        TileData data = tileDataMap.get(new Position(x, y));
        return data != null ? data.item : null;
    }

    @BetterComments(description = "Places an Item at a specific tile position. Works on all tiles except walls (tileId 2)", type="method")
    public boolean placeItemAt(int x, int y, Item item) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) {
            return false;
        }

        int tileId = mapData[y][x];
        if (tileId == 2) {
            System.out.println("Cannot place item on wall tile at (" + x + ", " + y + ")");
            return false;
        }

        TileData data = tileDataMap.get(new Position(x, y));
        if (data != null) {
            data.item = item;
            return true;
        }
        return false;
    }

    @BetterComments(description = "Removes and returns the Item from a specific tile position", type="method")
    public Item removeItemAt(int x, int y) {
        TileData data = tileDataMap.get(new Position(x, y));
        if (data != null && data.item != null) {
            Item removedItem = data.item;
            data.item = null;
            return removedItem;
        }
        return null;
    }

    @BetterComments(description = "Checks if a tile can hold items (all tiles except walls)", type = "method")
    public boolean canHoldItem(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) {
            return false;
        }
        int tileId = mapData[y][x];
        // Allow items on all tiles except walls (tileId == 2)
        return tileId != 2;
    }

    @BetterComments(description = "Removes tile data from a specific position", type="method")
    public void clearTileData(int x, int y) {
        tileDataMap.remove(new Position(x, y));
    }

    @BetterComments(description = "Prints the contents of tileDataMap for debugging", type="method")
    public void printTileDataMap() {
        System.out.println("=== Tile Data Map Contents ===");
        System.out.println("Total tiles in map: " + (MAP_WIDTH * MAP_HEIGHT));
        System.out.println("Total tiles with data: " + tileDataMap.size());
        System.out.println("\nAll Tiles:");

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                Station station = getStationAt(x, y);
                Item item = getItemAt(x, y);

                String stationName = station != null ? station.getClass().getSimpleName() : "null";
                String itemInfo = item != null ? item.getClass().getSimpleName() : "null";
                String canHold = canHoldItem(x, y) ? " [Can hold items]" : "";
                System.out.println("Position (" + x + ", " + y + ") - Tile ID: " + mapData[y][x] +
                                 " - Station: " + stationName + " - Item: " + itemInfo + canHold);
            }
        }
        System.out.println("==============================");
    }
}


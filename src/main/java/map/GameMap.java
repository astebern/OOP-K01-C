package map;

import core.GamePanel;
import utils.BetterComments;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class GameMap {
    GamePanel gp;
    Tile[] tiles;
    int[][] mapData;
    private static final int MAP_WIDTH = 14;
    private static final int MAP_HEIGHT = 10;

    @BetterComments(description = "Creates the tile array and map grid, loads tile graphics and collision settings, and initializes the kitchen layout",type="constructor")
    public GameMap(GamePanel gp){
        this.gp = gp;
        tiles = new Tile [11];
        mapData = new int[MAP_HEIGHT][MAP_WIDTH];
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

    }

    @BetterComments(description = "Loads all tile images from resources and configures which tiles are walkable or solid",type="method")
    public void getTileImage() {
        try{
            //floor(walkable space)
            tiles[0] =new Tile ();
            tiles[0].image = ImageIO.read(getClass().getResourceAsStream("/tile/floor_tile.png"));
            tiles[0].collision=false;
            //spawn point
            tiles[1] =new Tile ();
            tiles[1].image = ImageIO.read(getClass().getResourceAsStream("/tile/spawn_tile.png"));
            tiles[1].collision=false;
            //wall
            tiles[2] =new Tile ();
            tiles[2].image = ImageIO.read(getClass().getResourceAsStream("/tile/wall_tile.png"));
            tiles[2].collision=true;
            //Trash Station
            tiles[3] =new Tile ();
            tiles[3].image = ImageIO.read(getClass().getResourceAsStream("/tile/trash_tile.png"));
            tiles[3].collision=true;
            //Plate Storage
            tiles[4] =new Tile ();
            tiles[4].image = ImageIO.read(getClass().getResourceAsStream("/tile/plate_tile.png"));
            tiles[4].collision=true;
            //Ingredient Storage
            tiles[5] =new Tile ();
            tiles[5].image = ImageIO.read(getClass().getResourceAsStream("/tile/ingredient_tile.png"));
            tiles[5].collision=true;
            //Washing Station
            tiles[6] =new Tile ();
            tiles[6].image = ImageIO.read(getClass().getResourceAsStream("/tile/washing_tile.png"));
            tiles[6].collision=true;
            //Serving Counter
            tiles[7] =new Tile ();
            tiles[7].image = ImageIO.read(getClass().getResourceAsStream("/tile/serving_tile.png"));
            tiles[7].collision=true;
            //Assembly Station
            tiles[8] =new Tile ();
            tiles[8].image = ImageIO.read(getClass().getResourceAsStream("/tile/assembly_tile.png"));
            tiles[8].collision=true;
            //Cooking Station
            tiles[9] =new Tile ();
            tiles[9].image = ImageIO.read(getClass().getResourceAsStream("/tile/cooking_tile.png"));
            tiles[9].collision=true;
            //Cutting Station
            tiles[10] =new Tile ();
            tiles[10].image = ImageIO.read(getClass().getResourceAsStream("/tile/cutting_tile.png"));
            tiles[10].collision=true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @BetterComments(description = "Renders the entire tile map to the screen by drawing the correct tile image at each grid position",type="method")
    public void draw(Graphics2D g2){
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                int tileId = mapData[y][x];
                int screenX = x * gp.tileSize;
                int screenY = y * gp.tileSize;
                g2.drawImage(tiles[tileId].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
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
}


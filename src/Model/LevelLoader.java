package Model;

import Controller.GameController;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LevelLoader {
    private int size = 50;
    private BufferedImage[][] grassTile;
    private BufferedImage rockImg;
    private BufferedImage rockImg2;
    public static final int[][] world =  {// 24 col x 18 row (1=WALL, 2=GRASS, 3=ROCK1, 4=ROCK2)
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 0, 2, 1, 2, 2, 2, 2, 1, 2, 2, 3, 1, 1, 2, 3, 2, 2, 2, 2, 0, 1, 2, 0, 2, 2, 1, 2, 4, 2, 2, 2, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 1, 2, 1, 2, 1, 1, 0, 1, 2, 1, 2, 1, 1, 1, 3, 1, 2, 2, 2, 2, 1, 2, 0, 2, 2, 2, 1},
            {1, 2, 2, 0, 4, 2, 2, 2, 2, 2, 1, 0, 2, 2, 2, 0, 1, 2, 2, 2, 1, 3, 0, 2, 1, 2, 1, 2, 2, 0, 2, 1, 2, 2, 2, 2, 2, 1},
            {1, 2, 2, 0, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 0, 1, 1, 1, 2, 1, 0, 1, 2, 1, 2, 0, 0, 2, 2, 2, 0, 2, 2, 2, 4, 2, 1},
            {1, 0, 2, 2, 2, 2, 0, 2, 2, 2, 2, 0, 2, 0, 2, 0, 2, 0, 0, 0, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 3, 2, 2, 0, 2, 0, 2, 1},
            {1, 2, 4, 3, 2, 2, 2, 2, 1, 1, 0, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 2, 2, 3, 2, 1, 2, 2, 2, 2, 0, 1},
            {1, 2, 3, 3, 2, 2, 2, 2, 2, 2, 2, 1, 0, 2, 2, 1, 2, 0, 0, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 4, 2, 1, 0, 2, 2, 2, 2, 1},
            {1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 0, 0, 1, 2, 1, 1, 0, 1, 2, 1, 2, 1, 2, 0, 2, 2, 2, 0, 1, 2, 2, 2, 2, 0, 1},
            {1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 0, 2, 2, 1, 1, 1, 0, 1, 1, 2, 1, 2, 1, 4, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
    };
    public int getColumns() {
        return world[0].length;
    }
    public int getRows() {
        return world.length;
    }

    public LevelLoader() {
        BufferedImage tempImg = LoadSave.GetSpriteAtlas(LoadSave.GRASS_TILESET);
        grassTile = new BufferedImage[16][9];
        int subimageWidth = 256 / 16;
        int subimageHeight = 144 / 9;


        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 9; j++) {
                int subimageX = subimageWidth * i;
                int subimageY = subimageHeight * j;
                grassTile[i][j] = tempImg.getSubimage(subimageX, subimageY, subimageWidth, subimageHeight);
            }
        }
        rockImg = LoadSave.GetSpriteAtlas(LoadSave.STONE_1);
        rockImg2 = LoadSave.GetSpriteAtlas(LoadSave.STONE_2);
    }


    public void render(Graphics g, int xLvlOffset) {
        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[i].length; j++) {
                if (LevelLoader.world[i][j] == 0) {
                    g.drawImage(grassTile[1][3], GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
                }
                if (LevelLoader.world[i][j] == 3) {
                    g.drawImage(rockImg, GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
                }
                if (LevelLoader.world[i][j] == 4) {
                    g.drawImage(rockImg2, GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i, null);
                }
            }
        }
    }
    public Image getGrassImage() {
        return grassTile[1][3];
    }
    public Image getRock1Image() {
        return rockImg;
    }
    public Image getRock2Image() {
        return rockImg2;
    }

//    private void loadLevel() {
//        world = new int[size][size];
//
//        try (BufferedReader br = new BufferedReader(new FileReader("worldMapV1.txt"))) {
//            String line;
//            int row = 0;
//
//            while ((line = br.readLine()) != null && row < size) {
//                String[] values = line.trim().split("\\s+");
//
//                for (int col = 0; col < Math.min(size, values.length); col++) {
//                    world[row][col] = Integer.parseInt(values[col]);
//                }
//
//                row++;
//            }
//        } catch (IOException | NumberFormatException e) {
//            e.printStackTrace();
//            System.out.println("NO World File");
//        }
//    }

    public int[][] getWorld() {
        return world;
    }
}

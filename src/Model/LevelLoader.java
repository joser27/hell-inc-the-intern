package Model;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LevelLoader {
    private int size = 50;
    public static final int[][] world =  {// 24 col x 18 row (1=WALL, 2=GRASS, 3=ROCK1, 4=ROCK2)
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 2, 2, 2, 2, 2, 0, 2, 1, 2, 2, 2, 2, 1, 2, 2, 3, 1, 1, 2, 3, 2, 2, 2, 2, 0, 1},
            {1, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 1, 2, 1, 2, 1, 1, 0, 1, 2, 1, 2, 1, 1, 1, 3, 1},
            {1, 2, 2, 0, 4, 2, 2, 2, 2, 2, 1, 0, 2, 2, 2, 0, 1, 2, 2, 2, 1, 3, 0, 2, 1, 2, 1},
            {1, 2, 2, 0, 2, 2, 2, 2, 1, 1, 1, 2, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1},
            {1, 0, 2, 2, 2, 2, 0, 2, 2, 2, 2, 0, 2, 0, 2, 0, 2, 0, 0, 2, 2, 2, 1, 2, 2, 2, 1},
            {1, 2, 4, 3, 2, 2, 2, 2, 1, 1, 0, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1},
            {1, 2, 3, 3, 2, 2, 2, 2, 2, 2, 2, 1, 0, 2, 2, 1, 2, 0, 0, 2, 2, 2, 2, 2, 2, 0, 1},
            {1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 2, 1, 2, 0, 0, 1, 2, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1},
            {1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1},
            {1, 2, 1, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 2, 2, 2, 1, 3, 1, 2, 1},
            {1, 2, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 2, 1},
            {1, 2, 2, 3, 1, 2, 2, 2, 1, 2, 2, 2, 0, 1, 0, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1},
            {1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 0, 1, 1, 1, 1, 2, 1, 2, 1, 0, 1, 2, 1},
            {1, 2, 1, 2, 0, 0, 1, 2, 1, 2, 2, 2, 2, 2, 0, 2, 2, 2, 1, 2, 1, 2, 0, 0, 1, 2, 1},
            {1, 0, 1, 1, 1, 3, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 1, 2, 1},
            {1, 2, 2, 0, 2, 2, 1, 2, 1, 2, 1, 2, 2, 0, 1, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };

    public LevelLoader() {
        //loadLevel();
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

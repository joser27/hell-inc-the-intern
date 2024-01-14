package Controller;

import Model.Game;
import Model.LevelLoader;
import View.GameFrame;
import View.GamePanel;

import java.awt.*;

public class GameController {
    private Game game;
    private final int originalTileSize = 16; //16x16 tile
    private final int scale = 3;
    private final int tileSize = originalTileSize * scale; //48x48 tile
    public static final int maxScreenCol = 27;
    public static final int maxScreenRow = 18;
    final int screenWidth = maxScreenCol * tileSize;
    final int screenHeight = maxScreenRow * tileSize;

    private LevelLoader levelLoader;
    int[][] world;
    public GameController() {
        game = new Game(tileSize,tileSize);
        levelLoader = new LevelLoader();
        world = levelLoader.getWorld();
        GamePanel gamePanel = new GamePanel(this);
        GameLoop gameLoop = new GameLoop(game,gamePanel,this);
        gamePanel.setGameLoop(gameLoop);
        GameFrame gameFrame = new GameFrame(gamePanel);
        KeyboardInputs keyboardInputs = new KeyboardInputs(this);
        gamePanel.addKeyListener(keyboardInputs);

        gameLoop.startGameLoop();
    }

    void update() {
        game.update();
    }

    public void render(Graphics g) {
//        for (int row = 0; row < world.length; row++) {
//            for (int col = 0; col < world[row].length; col++) {
//                int cellValue = world[row][col];
//
//                // Check if the cell value is 4
//                if (cellValue == 1) {
//                    // Calculate the coordinates to draw the rectangle
//                    int x = col * tileSize;
//                    int y = row * tileSize;
//
//                    // Set the color to red and draw the filled rectangle
//                    g.setColor(Color.RED);
//                    g.fillRect(x, y, tileSize, tileSize);
//                }
//
//                // You can add additional logic here to render other elements based on their values
//            }
//        }
        game.render(g);
    }

    public Game getGame() {
        return game;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getTileSize() {
        return tileSize;
    }
}

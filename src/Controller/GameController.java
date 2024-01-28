package Controller;

import Model.Game;
import Model.LevelLoader;
import View.GameFrame;
import View.GamePanel;

import java.awt.*;

public class GameController {
    private Game game;
    private GamePanel gamePanel;
    private GameFrame gameFrame;
    private GameLoop gameLoop;
    private final int originalTileSize = 16; //16x16 tile
    private final int scale = 3;
    private final int tileSize = originalTileSize * scale; //48x48 tile
    public static final int maxScreenCol = 27;
    public static final int maxScreenRow = 18;
    final int screenWidth = maxScreenCol * tileSize;
    final int screenHeight = maxScreenRow * tileSize;
    private LevelLoader levelLoader;
    int[][] world;
    private int timer = 0;
    public GameController() {
        game = new Game(tileSize,tileSize);
        levelLoader = new LevelLoader();
        world = levelLoader.getWorld();
        gamePanel = new GamePanel(this);
        gameLoop = new GameLoop(game,gamePanel,this);
        gamePanel.setGameLoop(gameLoop);
        gameFrame = new GameFrame(gamePanel);
        KeyboardInputs keyboardInputs = new KeyboardInputs(this);
        gamePanel.addKeyListener(keyboardInputs);

        gameLoop.startGameLoop();
    }

    void update() {
        game.update();
        if (game.getPlayer2().getBullet() != null) {
            if (game.getPlayer2().getBullet().intersects(game.getPlayer1().getHitBox())) {
                game.getPlayer1().respawn();
            }
        }
        timer++;
        if (timer == 120) {
            timer = 0;
            game.setTime(1);
        }
        if (game.getTime() == 0) {
            gameLoop.stopGameLoop();
            gameFrame.gameOverScreen();
        }
        if (game.getPlayer1().getHitBox().intersects(game.getPlayer2().getHitBox())) {
            gameLoop.stopGameLoop();
            gameFrame.gameOverScreen();
        }
    }

    public void render(Graphics g) {

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

package Controller;

import Model.Game;
import Model.LandMine;
import Model.LevelLoader;
import Model.gamestates.Gamestate;
import Model.gamestates.LoadMenu;
import Model.gamestates.Playing;
import View.GameFrame;
import View.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class GameController {
    private LoadMenu menu;
    private Playing playing;
    private Game game;
    private GamePanel gamePanel;
    private GameFrame gameFrame;
    private GameLoop gameLoop;
    private static final int originalTileSize = 16; //16x16 tile
    private static final int scale = 3;
    private static final int tileSize = originalTileSize * scale; //48x48 tile
    public static final int maxScreenCol = 27;
    public static final int maxScreenRow = 18;
    public static final int screenWidth = maxScreenCol * tileSize;
    public static final int screenHeight = maxScreenRow * tileSize;
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
        playing = new Playing(game);
        menu = new LoadMenu(game);
        gameLoop.startGameLoop();

    }

    void update() {
        switch(Gamestate.state){
            case MENU -> menu.update();
            case PLAYING -> {
                playing.update();
                updateTools();
            }
        }
    }

    public void updateTools() {
        ArrayList<LandMine> landMine = game.getPlayer2().getLandMine();
        for (LandMine mine : landMine) {
            if (mine.getLandMineHitBox().intersects(game.getPlayer1().getHitBox())) {
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
        switch(Gamestate.state){
            case MENU -> menu.render(g);
            case PLAYING -> playing.render(g);
        }
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

    public LoadMenu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }
}

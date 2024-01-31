package Controller;

import Model.Game;
import Model.LevelLoader;
import Model.gamestates.GameOver;
import Model.gamestates.Gamestate;
import Model.gamestates.LoadMenu;
import Model.gamestates.Playing;
import View.GameFrame;
import View.GamePanel;

import java.awt.*;

public class GameController {
    private LoadMenu menuState;
    private Playing playingState;
    private GameOver gameOverState;
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
    public GameController() {
        game = new Game(tileSize,tileSize);
        levelLoader = new LevelLoader();
        world = levelLoader.getWorld();
        gamePanel = new GamePanel(this);
        gameLoop = new GameLoop(game,gamePanel,this);
        gamePanel.setGameLoop(gameLoop);
        gameFrame = new GameFrame(gamePanel);

        // Inputs
        KeyboardInputs keyboardInputs = new KeyboardInputs(this);
        MouseInputs mouseInputs = new MouseInputs(this);
        gamePanel.addKeyListener(keyboardInputs);
        gamePanel.addMouseListener(mouseInputs);
        gamePanel.addMouseMotionListener(mouseInputs);

        // Game States
        playingState = new Playing(game);
        menuState = new LoadMenu(game);
        gameOverState = new GameOver(game);
        gameLoop.startGameLoop();

    }

    void update() {
        switch(Gamestate.state){
            case MENU -> menuState.update();
            case GAMEOVER -> gameOverState.update();
            case PLAYING -> {
                playingState.update();
                gameOverUpdate();
            }
        }
    }

    private void gameOverUpdate() {
        if (getGame().isGameOver()) {
            Gamestate.state = Gamestate.GAMEOVER;
        }
    }


    public void render(Graphics g) {
        switch(Gamestate.state){
            case MENU -> menuState.render(g);
            case GAMEOVER -> {
                playingState.render(g);
                StringBuilder winner = new StringBuilder("");
                if (game.getPlayerWinner() == 1) {
                    winner.append("CHASER");
                } else {
                    winner.append("RUNNER");
                }

                gameOverState.setPlayerWinner(String.valueOf(winner));
                gameOverState.render(g);
            }
            case PLAYING -> playingState.render(g);

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

    public LoadMenu getMenuState() {
        return menuState;
    }

    public Playing getPlayingState() {
        return playingState;
    }
}

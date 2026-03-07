package Controller;

import Model.Game;
import Model.gamestates.*;
import View.GameFrame;
import View.GamePanel;
import View.PlayingView;
import View.PauseMenuView;
import View.LoadMenuView;
import View.GameOverView;

import java.awt.*;

public class GameController {
    private LoadMenu menuState;
    private Playing playingState;
    private GameOver gameOverState;
    private PauseMenu pauseMenu;
    private Game game;
    private GamePanel gamePanel;
    private GameFrame gameFrame;
    private GameLoop gameLoop;
    private final PlayingView playingView = new PlayingView();
    private final PauseMenuView pauseMenuView = new PauseMenuView();
    private final LoadMenuView loadMenuView = new LoadMenuView();
    private final GameOverView gameOverView = new GameOverView();
    private static final int originalTileSize = 16; //16x16 tile
    public static final int SCALE = 4;
    public static final int TILE_SIZE = originalTileSize * SCALE; //48x48 tile
    public static final int TILES_IN_WIDTH = 20;//27 original
    public static final int TILES_IN_HEIGHT = 11;//18 original
//    public static final int GAME_WIDTH = TILES_IN_WIDTH * TILE_SIZE;
//    public static final int GAME_HEIGHT = TILES_IN_HEIGHT * TILE_SIZE;

    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int GAME_WIDTH = (int) screenSize.getWidth();
    public static final int GAME_HEIGHT = (int) screenSize.getHeight();
    /** Zoom > 1 = zoomed in (smaller visible world). Lower value = zoomed out. */
    public static final float CAMERA_ZOOM = 1.25f;
//    public static final int GAME_WIDTH = (int) 1280;
//    public static final int GAME_HEIGHT = (int) 1024;

    public GameController() {
        game = new Game();
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
        pauseMenu = new PauseMenu(game);

        gameLoop.startGameLoop();

    }

    void update() {
        switch(Gamestate.state){
            case MENU -> menuState.update();
            case GAMEOVER -> gameOverState.update();
            case PAUSEMENU -> pauseMenu.update();
            case PLAYING -> {
                playingState.update();
            }
        }
    }




    public void render(Graphics g) {
        switch (Gamestate.state) {
            case MENU -> loadMenuView.render(g, menuState);
            case GAMEOVER -> {
                playingView.render(g, game, playingState);
                gameOverState.setPlayerWinner(game.getPlayerWinner());
                gameOverView.render(g, gameOverState);
            }
            case PLAYING -> playingView.render(g, game, playingState);
            case PAUSEMENU -> {
                playingView.render(g, game, playingState);
                pauseMenuView.render(g, pauseMenu);
            }
        }
    }

    public Game getGame() {
        return game;
    }

    public int getScreenWidth() {
        return GAME_WIDTH;
    }

    public int getScreenHeight() {
        return GAME_HEIGHT;
    }

    public int getTileSize() {
        return TILE_SIZE;
    }

    public LoadMenu getMenuState() {
        return menuState;
    }

    public Playing getPlayingState() {
        return playingState;
    }

    public GameOver getGameOverState() {
        return gameOverState;
    }

    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }
}

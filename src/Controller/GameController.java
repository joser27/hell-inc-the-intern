package Controller;

import Model.Game;
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
    public static final int SCALE = 4;
    public static final int TILE_SIZE = originalTileSize * SCALE; //48x48 tile
    public static final int TILES_IN_WIDTH = 20;//27 original
    public static final int TILES_IN_HEIGHT = 11;//18 original
    public static final int GAME_WIDTH = TILES_IN_WIDTH * TILE_SIZE;
    public static final int GAME_HEIGHT = TILES_IN_HEIGHT * TILE_SIZE;



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
            case PLAYING -> {
                //game.setGameOver(false);
                playingState.render(g);
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

}

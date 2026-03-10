package Controller;

import Model.Game;
import Model.gamestates.*;
import Model.utilz.SoundPlayer;
import View.GameFrame;
import View.GamePanel;
import View.PlayingView;
import View.PauseMenuView;
import View.LoadMenuView;
import View.ModeSelectView;
import View.DaySummaryView;
import View.GameOverView;
import View.LoadingView;
import View.OptionsView;
import View.AboutView;

import java.awt.*;

public class GameController {
    private static final GraphicsEnvironment GE = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private LoadMenu menuState;
    private ModeSelect modeSelectState;
    private DaySummary daySummaryState;
    private OptionsMenu optionsMenu;
    private AboutMenu aboutMenu;
    private Playing playingState;
    private GameOver gameOverState;
    private Loading loadingState;
    private PauseMenu pauseMenu;
    private Game game;
    private GamePanel gamePanel;
    private GameFrame gameFrame;
    private GameLoop gameLoop;
    private final PlayingView playingView = new PlayingView();
    private final PauseMenuView pauseMenuView = new PauseMenuView();
    private final LoadMenuView loadMenuView = new LoadMenuView();
    private final ModeSelectView modeSelectView = new ModeSelectView();
    private final DaySummaryView daySummaryView = new DaySummaryView();
    private final OptionsView optionsView = new OptionsView();
    private final AboutView aboutView = new AboutView();
    private final GameOverView gameOverView = new GameOverView();
    private final LoadingView loadingView = new LoadingView();
    private static final int originalTileSize = 16; //16x16 tile
    public static final int SCALE = 4;
    public static final int TILE_SIZE = originalTileSize * SCALE; //48x48 tile
    public static final int TILES_IN_WIDTH = 20;//27 original
    public static final int TILES_IN_HEIGHT = 11;//18 original
//    public static final int GAME_WIDTH = TILES_IN_WIDTH * TILE_SIZE;
//    public static final int GAME_HEIGHT = TILES_IN_HEIGHT * TILE_SIZE;

    /** Usable screen bounds (excludes Windows taskbar / menu bar) so the game is not clipped. */
    private static final Rectangle USABLE_SCREEN = GE.getMaximumWindowBounds();
    public static final int GAME_WIDTH = USABLE_SCREEN.width;
    public static final int GAME_HEIGHT = USABLE_SCREEN.height;
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
        gamePanel.addMouseWheelListener(mouseInputs);

        // Game States
        playingState = new Playing(game, this);
        menuState = new LoadMenu(game, this);
        optionsMenu = new OptionsMenu(game, this);
        aboutMenu = new AboutMenu(game, this);
        gameOverState = new GameOver(game);
        loadingState = new Loading(game);
        pauseMenu = new PauseMenu(game);
        modeSelectState = new ModeSelect(game, this);
        daySummaryState = new DaySummary(game);

        SoundPlayer.preloadKnock();
        SoundPlayer.preloadSteps();
        SoundPlayer.preloadRustle();
        gameLoop.startGameLoop();

    }

    void update() {
        boolean inMenuFlow = Gamestate.state == Gamestate.MENU
                || Gamestate.state == Gamestate.MODE_SELECT
                || Gamestate.state == Gamestate.OPTIONS
                || Gamestate.state == Gamestate.ABOUT;
        if (inMenuFlow) SoundPlayer.startMenuMusic();
        else SoundPlayer.stopMenuMusic();
        if (Gamestate.state == Gamestate.PLAYING && !game.isShowWidowFrame())
            SoundPlayer.startNightAmbience();
        else
            SoundPlayer.stopNightAmbience();
        switch(Gamestate.state){
            case MENU -> menuState.update();
            case MODE_SELECT -> modeSelectState.update();
            case OPTIONS -> optionsMenu.update();
            case ABOUT -> aboutMenu.update();
            case GAMEOVER -> gameOverState.update();
            case LOADING -> loadingState.update();
            case PAUSEMENU -> pauseMenu.update();
            case PLAYING -> {
                playingState.update();
                if (game.isQuotaJustMet()) {
                    daySummaryState.enter();
                    Gamestate.state = Gamestate.DAY_SUMMARY;
                } else if (game.isGameOver()) {
                    Gamestate.state = Gamestate.GAMEOVER;
                }
            }
            case DAY_SUMMARY -> daySummaryState.update();
        }
    }




    public void render(Graphics g) {
        switch (Gamestate.state) {
            case MENU -> loadMenuView.render(g, menuState, this);
            case MODE_SELECT -> modeSelectView.render(g, modeSelectState, this);
            case OPTIONS -> {
                if (optionsMenu.isOpenedFromPlaying()) {
                    playingView.render(g, game, playingState);
                    g.setColor(new Color(0, 0, 0, 140));
                    g.fillRect(0, 0, getDisplayWidth(), getDisplayHeight());
                }
                optionsView.render(g, optionsMenu, this);
            }
            case ABOUT -> aboutView.render(g, aboutMenu, this);
            case GAMEOVER -> {
                playingView.render(g, game, playingState, getDisplayWidth(), getDisplayHeight());
                gameOverState.setPlayerWinner(game.getPlayerWinner());
                gameOverView.render(g, gameOverState, game, getDisplayWidth(), getDisplayHeight());
            }
            case LOADING -> loadingView.render(g, loadingState, getDisplayWidth(), getDisplayHeight());
            case PLAYING -> playingView.render(g, game, playingState, getDisplayWidth(), getDisplayHeight());
            case DAY_SUMMARY -> {
                playingView.render(g, game, playingState, getDisplayWidth(), getDisplayHeight());
                daySummaryView.render(g, daySummaryState, game, getDisplayWidth(), getDisplayHeight());
            }
            case PAUSEMENU -> {
                playingView.render(g, game, playingState, getDisplayWidth(), getDisplayHeight());
                pauseMenuView.render(g, pauseMenu);
            }
        }
    }

    public void setFullscreen(boolean fullscreen) {
        gameFrame.setFullscreen(fullscreen);
    }

    public boolean isFullscreen() {
        return gameFrame.isFullscreen();
    }

    public void quitGame() {
        gameFrame.quit();
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

    /** Current panel size (fills fullscreen when maximized). Use for menu/options/about so they fill the window. */
    public int getDisplayWidth() {
        int w = gamePanel != null ? gamePanel.getWidth() : 0;
        return w > 0 ? w : GAME_WIDTH;
    }

    public int getDisplayHeight() {
        int h = gamePanel != null ? gamePanel.getHeight() : 0;
        return h > 0 ? h : GAME_HEIGHT;
    }

    public int getTileSize() {
        return TILE_SIZE;
    }

    public LoadMenu getMenuState() {
        return menuState;
    }

    public ModeSelect getModeSelectState() {
        return modeSelectState;
    }

    public DaySummary getDaySummaryState() {
        return daySummaryState;
    }

    public OptionsMenu getOptionsMenu() {
        return optionsMenu;
    }

    public AboutMenu getAboutMenu() {
        return aboutMenu;
    }

    public Playing getPlayingState() {
        return playingState;
    }

    public GameOver getGameOverState() {
        return gameOverState;
    }

    public Loading getLoadingState() {
        return loadingState;
    }

    public PauseMenu getPauseMenu() {
        return pauseMenu;
    }
}

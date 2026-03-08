package Controller;

import Model.gamestates.Gamestate;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInputs implements KeyListener {
    GameController gameController;
    public KeyboardInputs(GameController gameController) {
        this.gameController = gameController;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (Gamestate.state == Gamestate.PLAYING) {
            gameController.getPlayingState().keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().keyPressed(e);
            case OPTIONS -> gameController.getOptionsMenu().keyPressed(e);
            case ABOUT -> gameController.getAboutMenu().keyPressed(e);
            case PLAYING -> gameController.getPlayingState().keyPressed(e);
            case GAMEOVER -> gameController.getGameOverState().keyPressed(e);
            case LOADING -> gameController.getLoadingState().keyPressed(e);
            case PAUSEMENU -> gameController.getPauseMenu().keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().keyReleased(e);
            case PLAYING -> gameController.getPlayingState().keyReleased(e);
        }
    }
}

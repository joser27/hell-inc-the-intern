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

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().keyPressed(e);
            case PLAYING -> gameController.getPlayingState().keyPressed(e);
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

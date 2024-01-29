package Controller;

import Model.gamestates.Gamestate;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInputs implements MouseListener, MouseMotionListener {
    GameController gameController;
    public MouseInputs(GameController gameController) {
        this.gameController = gameController;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mouseClicked(e);
            case PLAYING -> gameController.getPlayingState().mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mousePressed(e);
            case PLAYING -> gameController.getPlayingState().mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mouseReleased(e);
            case PLAYING -> gameController.getPlayingState().mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mouseMoved(e);
            case PLAYING -> gameController.getPlayingState().mouseMoved(e);
        }
    }
}

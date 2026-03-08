package Controller;

import Model.EncounterState;
import Model.gamestates.Gamestate;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class MouseInputs implements MouseListener, MouseMotionListener, MouseWheelListener {
    GameController gameController;
    public MouseInputs(GameController gameController) {
        this.gameController = gameController;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mouseClicked(e);
            case OPTIONS -> gameController.getOptionsMenu().mouseClicked(e);
            case ABOUT -> gameController.getAboutMenu().mouseClicked(e);
            case PLAYING -> gameController.getPlayingState().mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mousePressed(e);
            case OPTIONS -> gameController.getOptionsMenu().mousePressed(e);
            case ABOUT -> gameController.getAboutMenu().mousePressed(e);
            case PLAYING -> gameController.getPlayingState().mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mouseReleased(e);
            case OPTIONS -> gameController.getOptionsMenu().mouseReleased(e);
            case ABOUT -> gameController.getAboutMenu().mouseReleased(e);
            case PLAYING -> gameController.getPlayingState().mouseReleased(e);
            case PAUSEMENU -> gameController.getPauseMenu().mouseReleased(e);
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
        if (Gamestate.state == Gamestate.OPTIONS)
            gameController.getOptionsMenu().mouseDragged(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (Gamestate.state == Gamestate.PLAYING) {
            EncounterState enc = gameController.getGame().getEncounterState();
            if (gameController.getGame().isShowWidowFrame() && enc != null) {
                // Wheel toward user (negative rotation) = see older lines; away = see newer
                enc.scroll(-e.getWheelRotation());
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        switch(Gamestate.state) {
            case MENU -> gameController.getMenuState().mouseMoved(e);
            case OPTIONS -> gameController.getOptionsMenu().mouseMoved(e);
            case ABOUT -> gameController.getAboutMenu().mouseMoved(e);
            case PLAYING -> gameController.getPlayingState().mouseMoved(e);
            case PAUSEMENU -> gameController.getPauseMenu().mouseMoved(e);
        }
    }
}

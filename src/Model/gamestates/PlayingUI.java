package Model.gamestates;

import Model.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/** Placeholder for playing-state UI; Demonic Contractor uses no ability bar. */
public class PlayingUI extends State implements Statemethods {

    public PlayingUI(Game game) {
        super(game);
    }

    @Override
    public void update() { }

    @Override
    public void render(Graphics g) { /* Rendering done by View.PlayingView */ }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

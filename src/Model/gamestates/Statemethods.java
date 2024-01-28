package Model.gamestates;

import java.awt.*;
import java.awt.event.KeyEvent;

public interface Statemethods {

    public void update();
    public void render(Graphics g);
    public void mouseClicked(Graphics g);
    public void mousePressed(Graphics g);
    public void mouseReleased(Graphics g);
    public void mouseMoved(Graphics g);
    public void keyPressed(KeyEvent e);
    public void keyReleased(KeyEvent e);
}

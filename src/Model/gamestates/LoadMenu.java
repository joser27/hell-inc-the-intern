package Model.gamestates;

import Controller.GameController;
import Model.Game;

import java.awt.*;
import java.awt.event.KeyEvent;

public class LoadMenu extends State implements Statemethods{
    public LoadMenu(Game game) {
        super(game);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("MENU", GameController.screenWidth/2,300);
    }

    @Override
    public void mouseClicked(Graphics g) {

    }

    @Override
    public void mousePressed(Graphics g) {

    }

    @Override
    public void mouseReleased(Graphics g) {

    }

    @Override
    public void mouseMoved(Graphics g) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER) {
            Gamestate.state = Gamestate.PLAYING;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

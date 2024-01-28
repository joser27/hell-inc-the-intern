package Model.gamestates;

import Model.Game;
import Model.Player;
import Model.Player1;
import Model.Player2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Playing extends State implements Statemethods{

    public Playing(Game game) {
        super(game);

    }

    @Override
    public void update() {
        getGame().update();
    }

    @Override
    public void render(Graphics g) {
        getGame().render(g);
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

        if (keyCode == KeyEvent.VK_W) {
            getGame().getPlayer1().setUp(true);
        }
        else if (keyCode == KeyEvent.VK_S) {
            getGame().getPlayer1().setDown(true);
        }
        else if (keyCode == KeyEvent.VK_A) {
            getGame().getPlayer1().setLeft(true);
        }
        else if (keyCode == KeyEvent.VK_D) {
            getGame().getPlayer1().setRight(true);
        }

        if (keyCode == KeyEvent.VK_UP) {
            getGame().getPlayer2().setUp(true);
        }
        else if (keyCode == KeyEvent.VK_DOWN) {
            getGame().getPlayer2().setDown(true);
        }
        else if (keyCode == KeyEvent.VK_LEFT) {
            getGame().getPlayer2().setLeft(true);
        }
        else if (keyCode == KeyEvent.VK_RIGHT) {
            getGame().getPlayer2().setRight(true);
        }

        if (keyCode == KeyEvent.VK_NUMPAD0) {
            getGame().getPlayer2().placeMine();
        }

        if (keyCode == KeyEvent.VK_F) {
            getGame().getPlayer1().speedBoost();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        //Player 1
        if (keyCode == KeyEvent.VK_W) {
            getGame().getPlayer1().setUp(false);
        }
        else if (keyCode == KeyEvent.VK_S) {
            getGame().getPlayer1().setDown(false);
        }
        else if (keyCode == KeyEvent.VK_A) {
            getGame().getPlayer1().setLeft(false);
        }
        else if (keyCode == KeyEvent.VK_D) {
            getGame().getPlayer1().setRight(false);
        }

        //Player 2
        if (keyCode == KeyEvent.VK_UP) {
            getGame().getPlayer2().setUp(false);
        }
        else if (keyCode == KeyEvent.VK_DOWN) {
            getGame().getPlayer2().setDown(false);
        }
        else if (keyCode == KeyEvent.VK_LEFT) {
            getGame().getPlayer2().setLeft(false);
        }
        else if (keyCode == KeyEvent.VK_RIGHT) {
            getGame().getPlayer2().setRight(false);
        }
    }
}

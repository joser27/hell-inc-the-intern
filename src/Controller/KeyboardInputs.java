package Controller;

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
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) {
            gameController.getGame().getPlayer1().setUp(true);
        }
        else if (keyCode == KeyEvent.VK_S) {
            gameController.getGame().getPlayer1().setDown(true);
        }
        else if (keyCode == KeyEvent.VK_A) {
            gameController.getGame().getPlayer1().setLeft(true);
        }
        else if (keyCode == KeyEvent.VK_D) {
            gameController.getGame().getPlayer1().setRight(true);
        }

        if (keyCode == KeyEvent.VK_UP) {
            gameController.getGame().getPlayer2().setUp(true);
        }
        else if (keyCode == KeyEvent.VK_DOWN) {
            gameController.getGame().getPlayer2().setDown(true);
        }
        else if (keyCode == KeyEvent.VK_LEFT) {
            gameController.getGame().getPlayer2().setLeft(true);
        }
        else if (keyCode == KeyEvent.VK_RIGHT) {
            gameController.getGame().getPlayer2().setRight(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        //Player 1
        if (keyCode == KeyEvent.VK_W) {
            gameController.getGame().getPlayer1().setUp(false);
        }
        else if (keyCode == KeyEvent.VK_S) {
            gameController.getGame().getPlayer1().setDown(false);
        }
        else if (keyCode == KeyEvent.VK_A) {
            gameController.getGame().getPlayer1().setLeft(false);
        }
        else if (keyCode == KeyEvent.VK_D) {
            gameController.getGame().getPlayer1().setRight(false);
        }

        //Player 2
        if (keyCode == KeyEvent.VK_UP) {
            gameController.getGame().getPlayer2().setUp(false);
        }
        else if (keyCode == KeyEvent.VK_DOWN) {
            gameController.getGame().getPlayer2().setDown(false);
        }
        else if (keyCode == KeyEvent.VK_LEFT) {
            gameController.getGame().getPlayer2().setLeft(false);
        }
        else if (keyCode == KeyEvent.VK_RIGHT) {
            gameController.getGame().getPlayer2().setRight(false);
        }
    }
}

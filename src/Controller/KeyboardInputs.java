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
            gameController.getGame().getPlayer().setUp(true);
        }
        else if (keyCode == KeyEvent.VK_S) {
            gameController.getGame().getPlayer().setDown(true);
        }
        else if (keyCode == KeyEvent.VK_A) {
            gameController.getGame().getPlayer().setLeft(true);
        }
        else if (keyCode == KeyEvent.VK_D) {
            gameController.getGame().getPlayer().setRight(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) {
            gameController.getGame().getPlayer().setUp(false);
        }
        else if (keyCode == KeyEvent.VK_S) {
            gameController.getGame().getPlayer().setDown(false);
        }
        else if (keyCode == KeyEvent.VK_A) {
            gameController.getGame().getPlayer().setLeft(false);
        }
        else if (keyCode == KeyEvent.VK_D) {
            gameController.getGame().getPlayer().setRight(false);
        }
    }
}

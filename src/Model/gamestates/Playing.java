package Model.gamestates;

import Controller.GameController;
import Model.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing extends State implements Statemethods {
    private int xLvlOffset;  // scaled offset so playerCenter*zoom - xLvlOffset = screen/2
    private int yLvlOffset;
    private PlayingUI ui;

    public Playing(Game game) {
        super(game);
        ui = new PlayingUI(game);
    }

    @Override
    public void update() {
        getGame().update();
        updateCameraCenteredOnPlayer();
    }

    /** Keeps the player at screen center. Java applies last-specified first, so (translate then scale) => (p*zoom - offset). So offset = playerCenter*zoom - screen/2. */
    private void updateCameraCenteredOnPlayer() {
        float zoom = GameController.CAMERA_ZOOM;
        float playerCenterX = (float) (getGame().getPlayer1().getHitBox().getX() + getGame().getPlayer1().getHitBox().getWidth() * 0.5);
        float playerCenterY = (float) (getGame().getPlayer1().getHitBox().getY() + getGame().getPlayer1().getHitBox().getHeight() * 0.5);
        xLvlOffset = Math.round(playerCenterX * zoom - GameController.GAME_WIDTH * 0.5f);
        yLvlOffset = Math.round(playerCenterY * zoom - GameController.GAME_HEIGHT * 0.5f);
    }

    @Override
    public void render(Graphics g) { /* Rendering done by View.PlayingView */ }

    public int getXLvlOffset() { return xLvlOffset; }
    public int getYLvlOffset() { return yLvlOffset; }
    public PlayingUI getPlayingUI() { return ui; }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) getGame().getPlayer1().setUp(true);
        else if (keyCode == KeyEvent.VK_S) getGame().getPlayer1().setDown(true);
        else if (keyCode == KeyEvent.VK_A) getGame().getPlayer1().setLeft(true);
        else if (keyCode == KeyEvent.VK_D) getGame().getPlayer1().setRight(true);
        if (keyCode == KeyEvent.VK_NUMPAD0) getGame().getPlayer1().attack();
        if (keyCode == KeyEvent.VK_NUMPAD1) getGame().getPlayer1().smashAttack(true);
        if (keyCode == KeyEvent.VK_NUMPAD2) getGame().getPlayer1().useShield();
        if (keyCode == KeyEvent.VK_NUMPAD3) getGame().getPlayer1().useRoar();
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (getGame().isShowWidowFrame()) {
                getGame().setShowWidowFrame(false);
            } else {
                Gamestate.state = Gamestate.PAUSEMENU;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_W) getGame().getPlayer1().setUp(false);
        else if (keyCode == KeyEvent.VK_S) getGame().getPlayer1().setDown(false);
        else if (keyCode == KeyEvent.VK_A) getGame().getPlayer1().setLeft(false);
        else if (keyCode == KeyEvent.VK_D) getGame().getPlayer1().setRight(false);
        if (keyCode == KeyEvent.VK_NUMPAD1) getGame().getPlayer1().smashAttack(false);
    }
}

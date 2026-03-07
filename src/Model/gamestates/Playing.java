package Model.gamestates;

import Controller.GameController;
import Model.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing extends State implements Statemethods {
    private int xLvlOffset;
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

    /** Keeps the camera centered on the player. Visible world size = screen size / zoom (zoomed in). */
    private void updateCameraCenteredOnPlayer() {
        float zoom = GameController.CAMERA_ZOOM;
        int playerX = (int) getGame().getPlayer1().getHitBox().getX();
        int playerY = (int) getGame().getPlayer1().getHitBox().getY();
        int playerW = (int) getGame().getPlayer1().getHitBox().getWidth();
        int playerH = (int) getGame().getPlayer1().getHitBox().getHeight();

        int levelWidth = LevelLoader.world[0].length * GameController.TILE_SIZE;
        int levelHeight = LevelLoader.world.length * GameController.TILE_SIZE;
        int visibleWorldW = (int) (GameController.GAME_WIDTH / zoom);
        int visibleWorldH = (int) (GameController.GAME_HEIGHT / zoom);
        int maxOffsetX = Math.max(0, levelWidth - visibleWorldW);
        int maxOffsetY = Math.max(0, levelHeight - visibleWorldH);

        xLvlOffset = playerX + playerW / 2 - visibleWorldW / 2;
        yLvlOffset = playerY + playerH / 2 - visibleWorldH / 2;
        xLvlOffset = Math.max(0, Math.min(maxOffsetX, xLvlOffset));
        yLvlOffset = Math.max(0, Math.min(maxOffsetY, yLvlOffset));
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
        if (keyCode == KeyEvent.VK_ESCAPE) Gamestate.state = Gamestate.PAUSEMENU;
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

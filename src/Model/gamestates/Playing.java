package Model.gamestates;

import Controller.GameController;
import Model.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing extends State implements Statemethods{
    private int xLvlOffset;
    private int leftBorder = (int) (0.4 * GameController.GAME_WIDTH);
    private int rightBorder = (int) (0.6 * GameController.GAME_WIDTH);
    private int lvlTilesWide = LevelLoader.world[0].length;
    private int maxTilesOffset = lvlTilesWide - GameController.TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset * GameController.TILE_SIZE;
    public Playing(Game game) {
        super(game);


    }

    @Override
    public void update() {
        getGame().update();
        checkCloseToBorder();
    }

    private void checkCloseToBorder() {
        int playerX = (int) getGame().getPlayer1().getHitBox().getX();
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;

    }

    @Override
    public void render(Graphics g) {
        getGame().render(g,xLvlOffset);
    }

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


        //Player 2 ability
        if (keyCode == KeyEvent.VK_NUMPAD0) {
            getGame().getPlayer2().placeMine();
        }
        if (keyCode == KeyEvent.VK_NUMPAD1) {
            getGame().getPlayer2().shoot();
        }

        //Player 1 ability
        if (keyCode == KeyEvent.VK_SPACE) {
            getGame().getPlayer1().speedBoost();
        }
        if (keyCode == KeyEvent.VK_F) {
            getGame().getPlayer1().attack();
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

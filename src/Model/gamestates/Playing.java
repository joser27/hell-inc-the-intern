package Model.gamestates;

import Controller.GameController;
import Model.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Playing extends State implements Statemethods{
    //Player 1
    private int p1xLvlOffset;
    private int p1leftBorder = (int) (0.75 * GameController.GAME_WIDTH);
    private int p1rightBorder = (int) (0.75 * GameController.GAME_WIDTH);
    private int p1lvlTilesWide = LevelLoader.world[0].length;
//    private int p1maxTilesOffset = p1lvlTilesWide - GameController.TILES_IN_WIDTH;
//    private int p1maxLvlOffsetX = p1maxTilesOffset * GameController.TILE_SIZE;

    // YLvlOffset
    private int p1yLvlOffset;
    private int p1topBorder = (int) (0.25 * GameController.GAME_WIDTH);
    private int p1downBorder = (int) (0.25 * GameController.GAME_WIDTH);
    private int p1lvlTilesHeight = LevelLoader.world.length;
//    private int p1maxYTilesOffset = p1lvlTilesWide - GameController.TILES_IN_WIDTH;
//    private int p1maxLvlOffsetX = p1maxTilesOffset * GameController.TILE_SIZE;

    //player 2
    private int p2xLvlOffset;
    private int p2leftBorder = (int) (0.25 * GameController.GAME_WIDTH);
    private int p2rightBorder = (int) (0.25 * GameController.GAME_WIDTH);
    private int p2lvlTilesWide = LevelLoader.world[0].length;
    private int p2maxTilesOffset = p2lvlTilesWide - GameController.TILES_IN_WIDTH;
    private int p2maxLvlOffsetX = p2maxTilesOffset * GameController.TILE_SIZE;

    //yLvlOffset
    private int p2yLvlOffset;
    private int p2topBorder = (int) (0.25 * GameController.GAME_WIDTH);
    private int p2downBorder = (int) (0.25 * GameController.GAME_WIDTH);
    private int p2lvlTilesHeight = LevelLoader.world.length;


    public Playing(Game game) {
        super(game);


    }

    @Override
    public void update() {
        getGame().update();
        p1checkCloseToBorder();
        p2checkCloseToBorder();
    }

    private void p1checkCloseToBorder() {
        int playerX = (int) getGame().getPlayer1().getHitBox().getX();
        int diff = playerX - p1xLvlOffset;

        if (diff > p1rightBorder)
            p1xLvlOffset += diff - p1rightBorder;
        else if (diff < p1leftBorder)
            p1xLvlOffset += diff - p1leftBorder;

        int playerY = (int) getGame().getPlayer1().getHitBox().getY();
        int diffY = playerY - p1yLvlOffset;

        if (diffY > p1topBorder)
            p1yLvlOffset += diffY - p1topBorder;
        else if (diffY < p1downBorder)
            p1yLvlOffset += diffY - p1downBorder;

//        if (xLvlOffset > maxLvlOffsetX)
//            xLvlOffset = maxLvlOffsetX;
//        else if (xLvlOffset < 0)
//            xLvlOffset = 0;
    }
    private void p2checkCloseToBorder() {
        int playerX = (int) getGame().getPlayer2().getHitBox().getX();
        int diff = playerX - p2xLvlOffset;

        if (diff > p2rightBorder)
            p2xLvlOffset += diff - p2rightBorder;
        else if (diff < p2leftBorder)
            p2xLvlOffset += diff - p2leftBorder;

        int playerY = (int) getGame().getPlayer2().getHitBox().getY();
        int diffY = playerY - p2yLvlOffset;

        if (diffY > p2topBorder)
            p2yLvlOffset += diffY - p2topBorder;
        else if (diffY < p2downBorder)
            p2yLvlOffset += diffY - p2downBorder;
//        if (xLvlOffset > maxLvlOffsetX)
//            xLvlOffset = maxLvlOffsetX;
//        else if (xLvlOffset < 0)
//            xLvlOffset = 0;
    }

    @Override
    public void render(Graphics g) {
//        getGame().render(g,xLvlOffset);
//
        g.setColor(Color.BLACK);
        g.drawLine(GameController.GAME_WIDTH/2,0,GameController.GAME_WIDTH/2,GameController.GAME_HEIGHT);
//        g.setColor(Color.BLACK);
//        g.fillRect(0,0,GameController.GAME_WIDTH/2,GameController.GAME_HEIGHT);

        getGame().renderLeftScreen(g, p2xLvlOffset, p2yLvlOffset);
        getGame().renderRightScreen(g, p1xLvlOffset, p1yLvlOffset);
        g.setColor(Color.GREEN);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawLine(GameController.GAME_WIDTH / 2, 0, GameController.GAME_WIDTH / 2, GameController.GAME_HEIGHT);
        g2d.setStroke(new BasicStroke(1));
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

        //Pause menu
        if (keyCode == KeyEvent.VK_ESCAPE) {
            Gamestate.state = Gamestate.PAUSEMENU;
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

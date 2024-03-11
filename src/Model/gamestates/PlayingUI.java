package Model.gamestates;

import Controller.GameController;
import Model.Game;
import Model.LevelLoader;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PlayingUI extends State implements Statemethods {

    Image player1_Q;

    public PlayingUI(Game game) {//(GameController.GAME_HEIGHT/2) + GameController.GAME_HEIGHT/4
        super(game);


    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        //Player 1 (Right screen)
        g.setColor(new Color(100,200,100,200));
        g.fillRect(GameController.GAME_WIDTH/2,(GameController.GAME_HEIGHT/2) + GameController.GAME_HEIGHT/4,GameController.GAME_WIDTH/2,GameController.GAME_HEIGHT/6);

        g.drawImage(player1_Q,200,200,null);

        //Player 2 (Left screen)
        g.setColor(new Color(200,100,100,200));
        g.fillRect(0,(GameController.GAME_HEIGHT/2) + GameController.GAME_HEIGHT/4,GameController.GAME_WIDTH/2,GameController.GAME_HEIGHT/6);


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

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

package Model.gamestates;

import Controller.GameController;
import Model.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class GameOver extends State implements Statemethods {
    private int playerWinner;
    public GameOver(Game game) {
        super(game);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        Color transparentBlack = new Color(0, 0, 0, 128);
        g.setColor(transparentBlack);
        g.fillRect(500, 300, 300, 300);
        g.setColor(Color.WHITE);

        g.drawString("GAME OVER!!", 580, 400);
        g.drawString("PLAYER " +  playerWinner + " WINS!", 570, 450);
//        g.drawString("WINS", 620, 500);
        g.drawString("PRESS ENTER TO PLAY AGAIN", 550, 490);
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
        if (keyCode == KeyEvent.VK_ENTER) {
            Gamestate.state = Gamestate.PLAYING;
            getGame().setGameOver(false);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void setPlayerWinner(int playerWinner) {
        this.playerWinner = playerWinner;
    }
}

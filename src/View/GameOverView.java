package View;

import Model.gamestates.GameOver;

import java.awt.*;

public class GameOverView {
    public void render(Graphics g, GameOver gameOver) {
        if (gameOver == null) return;
        Color transparentBlack = new Color(0, 0, 0, 128);
        g.setColor(transparentBlack);
        g.fillRect(500, 300, 300, 300);
        g.setColor(Color.WHITE);
        g.drawString("GAME OVER!!", 580, 400);
        g.drawString("PLAYER " + gameOver.getPlayerWinner() + " WINS!", 570, 450);
        g.drawString("PRESS ENTER TO PLAY AGAIN", 550, 490);
    }
}

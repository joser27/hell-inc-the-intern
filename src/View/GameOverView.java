package View;

import Model.gamestates.GameOver;

import java.awt.*;

public class GameOverView {
    public void render(Graphics g, GameOver gameOver) {
        if (gameOver == null) return;
        boolean won = gameOver.getPlayerWinner() == 1;
        Color transparentBlack = new Color(0, 0, 0, 160);
        g.setColor(transparentBlack);
        g.fillRect(480, 280, 340, 260);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.drawString("GAME OVER", 550, 330);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        if (won) {
            g.setColor(new Color(200, 255, 200));
            g.drawString("You reached your soul quota.", 520, 380);
            g.drawString("Your demonic master is pleased.", 510, 405);
        } else {
            g.setColor(new Color(255, 200, 200));
            g.drawString("The Church Investigator gathered", 500, 380);
            g.drawString("enough evidence to banish you.", 505, 405);
        }
        g.setColor(new Color(220, 220, 220));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("PRESS ENTER TO PLAY AGAIN", 530, 470);
    }
}

package View;

import Controller.GameController;
import Model.gamestates.GameOver;

import java.awt.*;

public class GameOverView {
    private static final int PANEL_W = 560;
    private static final int PANEL_H = 340;
    private static final int TITLE_FONT = 48;
    private static final int BODY_FONT = 22;
    private static final int HINT_FONT = 18;
    private static final int PAD = 40;
    private static final int LINE_GAP = 28;

    public void render(Graphics g, GameOver gameOver) {
        if (gameOver == null) return;
        int screenW = GameController.GAME_WIDTH;
        int screenH = GameController.GAME_HEIGHT;
        int x = (screenW - PANEL_W) / 2;
        int y = (screenH - PANEL_H) / 2;

        Color transparentBlack = new Color(0, 0, 0, 180);
        g.setColor(transparentBlack);
        g.fillRoundRect(x, y, PANEL_W, PANEL_H, 16, 16);
        g.setColor(new Color(80, 60, 100, 180));
        g.drawRoundRect(x, y, PANEL_W, PANEL_H, 16, 16);

        int cx = x + PANEL_W / 2;
        int textY = y + PAD + 36;

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, TITLE_FONT));
        FontMetrics titleFm = g.getFontMetrics();
        String title = "GAME OVER";
        g.drawString(title, cx - titleFm.stringWidth(title) / 2, textY);

        textY += LINE_GAP + 20;
        g.setFont(new Font("SansSerif", Font.PLAIN, BODY_FONT));
        FontMetrics bodyFm = g.getFontMetrics();
        boolean won = gameOver.getPlayerWinner() == 1;
        if (won) {
            g.setColor(new Color(200, 255, 200));
            String l1 = "You reached your soul quota.";
            String l2 = "Your demonic master is pleased.";
            g.drawString(l1, cx - bodyFm.stringWidth(l1) / 2, textY);
            textY += LINE_GAP;
            g.drawString(l2, cx - bodyFm.stringWidth(l2) / 2, textY);
        } else {
            g.setColor(new Color(255, 200, 200));
            String l1 = "The Church Investigator gathered";
            String l2 = "enough evidence to banish you.";
            g.drawString(l1, cx - bodyFm.stringWidth(l1) / 2, textY);
            textY += LINE_GAP;
            g.drawString(l2, cx - bodyFm.stringWidth(l2) / 2, textY);
        }

        textY = y + PANEL_H - PAD - 10;
        g.setColor(new Color(220, 220, 220));
        g.setFont(new Font("SansSerif", Font.PLAIN, HINT_FONT));
        String hint = "Press ENTER or ESC to return to main menu";
        int hintW = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, cx - hintW / 2, textY);
    }
}

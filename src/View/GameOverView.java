package View;

import Controller.GameController;
import Model.Game;
import Model.gamestates.GameOver;

import java.awt.*;

public class GameOverView {
    private static final int PANEL_W = 600;
    private static final int PANEL_H = 380;
    private static final int TITLE_FONT = 48;
    private static final int BODY_FONT = 22;
    private static final int STAT_FONT = 16;
    private static final int HINT_FONT = 18;
    private static final int PAD = 40;
    private static final int LINE_GAP = 28;

    public void render(Graphics g, GameOver gameOver, Game game, int displayW, int displayH) {
        if (gameOver == null) return;
        int x = (displayW - PANEL_W) / 2;
        int y = (displayH - PANEL_H) / 2;

        g.setColor(new Color(0, 0, 0, 180));
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
            drawCentered(g, bodyFm, "Quota met! Gary is finally pleased.", cx, textY); textY += LINE_GAP;
            drawCentered(g, bodyFm, "Promotion incoming.", cx, textY);
        } else {
            g.setColor(new Color(255, 200, 200));
            drawCentered(g, bodyFm, "Town suspicion hit 100%.", cx, textY); textY += LINE_GAP;
            drawCentered(g, bodyFm, "You've been pulled from the assignment.", cx, textY);
        }

        if (game != null) {
            textY += LINE_GAP + 12;
            g.setFont(new Font("SansSerif", Font.PLAIN, STAT_FONT));
            g.setColor(new Color(180, 170, 200));
            FontMetrics statFm = g.getFontMetrics();
            String stats = "Souls: " + game.getSouls() + "/" + game.getSoulQuota()
                    + "  |  Total: " + game.getTotalSouls()
                    + "  |  Suspicion: " + (int) game.getSuspicion() + "%";
            drawCentered(g, statFm, stats, cx, textY);
        }

        textY = y + PANEL_H - PAD - 10;
        g.setColor(new Color(220, 220, 220));
        g.setFont(new Font("SansSerif", Font.PLAIN, HINT_FONT));
        String hint = "Press ENTER or ESC to return to main menu";
        drawCentered(g, g.getFontMetrics(), hint, cx, textY);
    }

    /** Backward-compatible overload for callers that don't pass game/display. */
    public void render(Graphics g, GameOver gameOver) {
        render(g, gameOver, null, GameController.GAME_WIDTH, GameController.GAME_HEIGHT);
    }

    private void drawCentered(Graphics g, FontMetrics fm, String text, int cx, int y) {
        g.drawString(text, cx - fm.stringWidth(text) / 2, y);
    }
}

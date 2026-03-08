package View;

import Controller.GameController;
import Model.gamestates.OptionsMenu;

import java.awt.*;

public class OptionsView {
    public void render(Graphics g, OptionsMenu menu, GameController controller) {
        if (menu == null || controller == null) return;
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();

        if (!menu.isOpenedFromPlaying()) {
            g.setColor(new Color(18, 12, 28));
            g.fillRect(0, 0, w, h);
        }

        g.setFont(new Font("Serif", Font.BOLD, 42));
        g.setColor(new Color(220, 200, 255));
        String title = "Options";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (w - tw) / 2, (int) (h * 0.2));

        g.setFont(new Font("Serif", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        String fullscreenLabel = menu.isFullscreen() ? "Fullscreen: On" : "Fullscreen: Off";
        drawButton(g, menu.getButtonBounds(0), fullscreenLabel, fm, menu.getHoveredButton() == 0);

        String resumeOrBack = menu.isOpenedFromPlaying() ? "Resume" : "Back";
        drawButton(g, menu.getButtonBounds(1), resumeOrBack, fm, menu.getHoveredButton() == 1);

        if (menu.isOpenedFromPlaying()) {
            drawButton(g, menu.getButtonBounds(2), "Main menu", fm, menu.getHoveredButton() == 2);
        }

        g.setFont(new Font("Serif", Font.PLAIN, 14));
        g.setColor(new Color(160, 150, 180));
        String escHint = menu.isOpenedFromPlaying() ? "Press ESC to resume" : "Press ESC to go back";
        g.drawString(escHint, w / 2 - 60, h - 40);
    }

    private void drawButton(Graphics g, Rectangle r, String label, FontMetrics fm, boolean hover) {
        int x = r.x, y = r.y, w = r.width, h = r.height;
        if (hover) {
            g.setColor(new Color(80, 50, 120, 200));
            g.fillRoundRect(x, y, w, h, 10, 10);
            g.setColor(new Color(200, 180, 255, 220));
            g.drawRoundRect(x, y, w, h, 10, 10);
        } else {
            g.setColor(new Color(40, 28, 60, 200));
            g.fillRoundRect(x, y, w, h, 10, 10);
            g.setColor(new Color(120, 100, 160, 200));
            g.drawRoundRect(x, y, w, h, 10, 10);
        }
        g.setColor(hover ? Color.WHITE : new Color(230, 220, 255));
        int tx = x + (w - fm.stringWidth(label)) / 2;
        int ty = y + (h + fm.getAscent()) / 2 - 2;
        g.drawString(label, tx, ty);
    }
}

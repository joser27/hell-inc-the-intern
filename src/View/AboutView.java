package View;

import Controller.GameController;
import Model.gamestates.AboutMenu;

import java.awt.*;

public class AboutView {
    private static final String CREDIT = "Made by Jose Rodriguez";

    public void render(Graphics g, AboutMenu menu, GameController controller) {
        if (menu == null || controller == null) return;
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();

        g.setColor(new Color(18, 12, 28));
        g.fillRect(0, 0, w, h);

        g.setFont(new Font("Serif", Font.BOLD, 38));
        g.setColor(new Color(220, 200, 255));
        String title = "About";
        int tw = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (w - tw) / 2, (int) (h * 0.25));

        g.setFont(new Font("Serif", Font.PLAIN, 24));
        g.setColor(new Color(200, 190, 220));
        int cw = g.getFontMetrics().stringWidth(CREDIT);
        g.drawString(CREDIT, (w - cw) / 2, (int) (h * 0.5));

        Rectangle back = menu.getBackButtonBounds();
        boolean hover = (menu.getHoveredButton() == AboutMenu.BUTTON_BACK);
        g.setFont(new Font("Serif", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        drawButton(g, back, "Back", fm, hover);

        g.setFont(new Font("Serif", Font.PLAIN, 14));
        g.setColor(new Color(160, 150, 180));
        g.drawString("Press ESC to return", w / 2 - 70, h - 40);
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

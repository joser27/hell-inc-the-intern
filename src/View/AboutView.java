package View;

import Controller.GameController;
import Model.gamestates.AboutMenu;

import java.awt.*;

public class AboutView {
    private static final String CREDIT = "Made by Jose Rodriguez";
    private static final String CHAR_ART = "Character art: Hana Caraka - Base Character Otterisk";
    private static final String WORLD_ART = "World art: bonkyd";
    private static final String MUSIC = "Music: \uD83C\uDF42 Cozy Tunes by pizzadoggy";

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
        g.drawString(CREDIT, (w - cw) / 2, (int) (h * 0.38));

        g.setFont(new Font("Serif", Font.PLAIN, 18));
        g.setColor(new Color(180, 170, 210));
        int lineHeight = g.getFontMetrics().getHeight() + 6;
        int creditsY = (int) (h * 0.48);
        drawCentered(g, CHAR_ART, w, creditsY);
        drawCentered(g, WORLD_ART, w, creditsY + lineHeight);
        drawCentered(g, MUSIC, w, creditsY + lineHeight * 2);

        Rectangle back = menu.getBackButtonBounds();
        boolean hover = (menu.getHoveredButton() == AboutMenu.BUTTON_BACK);
        g.setFont(new Font("Serif", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        drawButton(g, back, "Back", fm, hover);

        g.setFont(new Font("Serif", Font.PLAIN, 14));
        g.setColor(new Color(160, 150, 180));
        g.drawString("Press ESC to return", w / 2 - 70, h - 40);
    }

    private void drawCentered(Graphics g, String text, int displayWidth, int y) {
        int tw = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (displayWidth - tw) / 2, y);
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

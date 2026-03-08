package View;

import Controller.GameController;
import Model.gamestates.LoadMenu;

import java.awt.*;

/**
 * Renders the main menu with gothic background, game title, and Play / Options / About buttons.
 */
public class LoadMenuView {
    private static final String GAME_TITLE = "Demonic Contractor";
    private static final String[] BUTTON_LABELS = { "Play", "Options", "About", "Quit" };

    public void render(Graphics g, LoadMenu menu, GameController controller) {
        if (menu == null || controller == null) return;
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();

        // Background — scale to fill screen
        Image bg = menu.getBackgroundImage();
        if (bg != null) {
            g.drawImage(bg, 0, 0, w, h, null);
        } else {
            g.setColor(new Color(20, 15, 35));
            g.fillRect(0, 0, w, h);
        }

        // Dark overlay so title and buttons stand out
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, w, h);

        // Title at top center — gothic, prominent
        g.setFont(new Font("Serif", Font.BOLD, 56));
        FontMetrics titleFm = g.getFontMetrics();
        int titleWidth = titleFm.stringWidth(GAME_TITLE);
        int titleX = (w - titleWidth) / 2;
        int titleY = (int) (h * 0.22);
        g.setColor(new Color(0, 0, 0, 180));
        g.drawString(GAME_TITLE, titleX + 3, titleY + 3);
        g.setColor(new Color(220, 200, 255));
        g.drawString(GAME_TITLE, titleX, titleY);
        g.setColor(new Color(180, 160, 220, 120));
        g.drawString(GAME_TITLE, titleX - 1, titleY - 1);

        // Subtitle
        g.setFont(new Font("Serif", Font.ITALIC, 18));
        g.setColor(new Color(200, 190, 220, 200));
        String sub = "Collect souls. Avoid the Investigator.";
        int subW = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (w - subW) / 2, titleY + 36);

        // Buttons
        g.setFont(new Font("Serif", Font.BOLD, 24));
        FontMetrics btnFm = g.getFontMetrics();
        for (int i = 0; i < 4; i++) {
            Rectangle r = menu.getButtonBounds(i);
            boolean hover = (menu.getHoveredButton() == i);
            drawButton(g, r, BUTTON_LABELS[i], btnFm, hover);
        }
    }

    private void drawButton(Graphics g, Rectangle r, String label, FontMetrics fm, boolean hover) {
        int x = r.x;
        int y = r.y;
        int w = r.width;
        int h = r.height;

        // Panel
        if (hover) {
            g.setColor(new Color(80, 50, 120, 200));
            g.fillRoundRect(x, y, w, h, 10, 10);
            g.setColor(new Color(200, 180, 255, 220));
            g.drawRoundRect(x, y, w, h, 10, 10);
        } else {
            g.setColor(new Color(30, 20, 50, 180));
            g.fillRoundRect(x, y, w, h, 10, 10);
            g.setColor(new Color(120, 100, 160, 200));
            g.drawRoundRect(x, y, w, h, 10, 10);
        }

        // Label centered
        int tx = x + (w - fm.stringWidth(label)) / 2;
        int ty = y + (h + fm.getAscent()) / 2 - 2;
        g.setColor(hover ? new Color(255, 250, 255) : new Color(230, 220, 255));
        g.drawString(label, tx, ty);
    }
}

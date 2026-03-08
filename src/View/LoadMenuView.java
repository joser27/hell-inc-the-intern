package View;

import Controller.GameController;
import Model.gamestates.LoadMenu;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Main menu in GTA-style: black background, menu frame image, intern/contractor portrait (9:16),
 * title and buttons with same look as encounter (dialogue box style, outline).
 */
public class LoadMenuView {
    private static final String GAME_TITLE = "Hell Inc.";
    private static final String SUBTITLE = "The Intern — Meet your quota. Don't get fired.";
    private static final String[] BUTTON_LABELS = { "Play", "Options", "About", "Quit" };

    private BufferedImage demonPortraitImage;

    public void render(Graphics g, LoadMenu menu, GameController controller) {
        if (menu == null || controller == null) return;
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();

        // 1. Black background (same as encounter)
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        // 2. Menu background image — centered frame, ~52% width like encounter door
        Image bg = menu.getBackgroundImage();
        if (bg != null) {
            float frameWidth = w * 0.52f;
            float frameHeight = frameWidth * (9f / 16f);
            int frameX = (int) ((w - frameWidth) / 2);
            int frameY = (int) ((h - frameHeight) / 2);
            g.drawImage(bg, frameX, frameY, (int) frameWidth, (int) frameHeight, null);
        }

        // 3. Demon portrait — 9:16, bottom-anchored, 14% shift right (match encounter)
        if (demonPortraitImage == null) {
            try {
                demonPortraitImage = LoadSave.GetSpriteAtlas(LoadSave.DEMON_PORTRAIT);
            } catch (Exception ignored) { }
        }
        if (demonPortraitImage != null) {
            float portraitHeight = h * 0.88f;
            float portraitWidth = portraitHeight * (9f / 16f);
            int portraitX = (int) ((w - portraitWidth) / 2 + w * 0.14f);
            int portraitY = (int) (h - portraitHeight);
            g.drawImage(demonPortraitImage, portraitX, portraitY, (int) portraitWidth, (int) portraitHeight, null);
        }

        // 4. Title — SansSerif, white, shadow (encounter-style)
        g.setFont(new Font("SansSerif", Font.BOLD, 52));
        FontMetrics titleFm = g.getFontMetrics();
        int titleWidth = titleFm.stringWidth(GAME_TITLE);
        int titleX = (w - titleWidth) / 2;
        int titleY = (int) (h * 0.18);
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(GAME_TITLE, titleX + 2, titleY + 2);
        g.setColor(Color.WHITE);
        g.drawString(GAME_TITLE, titleX, titleY);

        // 5. Subtitle
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(new Color(255, 255, 255, 200));
        int subW = g.getFontMetrics().stringWidth(SUBTITLE);
        g.drawString(SUBTITLE, (w - subW) / 2, titleY + 36);

        // 6. Buttons — encounter dialogue box style: dark fill + 2px white outline
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics btnFm = g.getFontMetrics();
        for (int i = 0; i < 4; i++) {
            Rectangle r = menu.getButtonBounds(i);
            boolean hover = (menu.getHoveredButton() == i);
            drawButton(g, r, BUTTON_LABELS[i], btnFm, hover);
        }
    }

    private void drawButton(Graphics g, Rectangle r, String label, FontMetrics fm, boolean hover) {
        int x = r.x, y = r.y, w = r.width, h = r.height;
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setColor(hover ? new Color(255, 255, 255, 240) : new Color(255, 255, 255, 200));
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, w, h, 12, 12);
        } else {
            g.drawRoundRect(x, y, w, h, 12, 12);
        }
        g.setColor(hover ? Color.WHITE : new Color(255, 255, 255, 230));
        int tx = x + (w - fm.stringWidth(label)) / 2;
        int ty = y + (h + fm.getAscent()) / 2 - 2;
        g.drawString(label, tx, ty);
    }
}

package View;

import Controller.GameController;
import Model.gamestates.ModeSelect;

import java.awt.*;

/**
 * Renders the mode selection screen: Campaign, Endless, How to Play toggle, Back.
 * Same visual style as the main menu (black bg, white outlines, encounter-style buttons).
 */
public class ModeSelectView {

    private static final String TITLE = "Choose Your Assignment";
    private static final String[] BUTTON_LABELS = { "Campaign", "Endless", "How to Play", "Back" };
    private static final String[] BUTTON_DESCS = {
            "One assignment: 7 souls. Get them before suspicion hits 100%.",
            "Endless assignments. Quota rises each time. How long can you last?",
            "",
            ""
    };

    private static final String[] HOW_TO_PLAY = {
        "THE GOAL",
        "  You're a demon intern with a clipboard and a quota.",
        "  Get residents to sign your contract — they think they're",
        "  getting something they want. They are not reading the fine print.",
        "",
        "HOW IT WORKS",
        "  Every resident wants something — money, revenge, love, fame.",
        "  Figure out what they want, offer it, get them to sign.",
        "  They think they're signing for the thing you promised.",
        "  They are actually signing their soul over to Hell Inc.",
        "  You don't need to mention souls. That's the whole point.",
        "",
        "SUSPICION",
        "  Every signed deal raises town suspicion.",
        "  Slammed doors raise it too — failed pitches spread rumors.",
        "  Hit 100% and the town reports you. Gary is disappointed.",
        "",
        "CONTROLS",
        "  WASD — Move   |   E — Knock   |   Type + ENTER — Talk   |   ESC — Leave",
        "",
        "Tip: Listen carefully. Residents drop hints about what they want.",
        "Tip: The contract has a lot of fine print. They never read it.",
    };

    public void render(Graphics g, ModeSelect state, GameController controller) {
        int w = controller.getDisplayWidth();
        int h = controller.getDisplayHeight();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        FontMetrics titleFm = g.getFontMetrics();
        int titleW = titleFm.stringWidth(TITLE);
        int titleY = (int) (h * 0.14);
        g.setColor(new Color(0, 0, 0, 200));
        g.drawString(TITLE, (w - titleW) / 2 + 2, titleY + 2);
        g.setColor(Color.WHITE);
        g.drawString(TITLE, (w - titleW) / 2, titleY);

        // Subtitle
        g.setFont(new Font("SansSerif", Font.ITALIC, 16));
        g.setColor(new Color(255, 255, 255, 160));
        String sub = "\"The horror is not monsters. It is quarterly targets.\"";
        int subW = g.getFontMetrics().stringWidth(sub);
        g.drawString(sub, (w - subW) / 2, titleY + 32);

        // Buttons
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        FontMetrics btnFm = g.getFontMetrics();
        for (int i = 0; i < BUTTON_LABELS.length; i++) {
            Rectangle r = state.getButtonBounds(i);
            boolean hover = state.getHoveredButton() == i;
            boolean active = (i == ModeSelect.BTN_HOW_TO_PLAY && state.isShowingHowToPlay());
            drawButton(g, r, BUTTON_LABELS[i], btnFm, hover, active);

            // Description below Campaign / Endless
            if (i < BUTTON_DESCS.length && !BUTTON_DESCS[i].isEmpty()) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g.setColor(new Color(200, 190, 220, hover ? 220 : 140));
                FontMetrics descFm = g.getFontMetrics();
                int descW = descFm.stringWidth(BUTTON_DESCS[i]);
                g.drawString(BUTTON_DESCS[i], r.x + (r.width - descW) / 2, r.y + r.height + 18);
                g.setFont(new Font("SansSerif", Font.BOLD, 22));
            }
        }

        // How to Play panel
        if (state.isShowingHowToPlay()) {
            drawHowToPlay(g, w, h);
        }
    }

    private void drawButton(Graphics g, Rectangle r, String label, FontMetrics fm, boolean hover, boolean active) {
        int x = r.x, y = r.y, bw = r.width, bh = r.height;
        g.setColor(active ? new Color(40, 20, 60, 200) : new Color(0, 0, 0, 160));
        g.fillRoundRect(x, y, bw, bh, 12, 12);
        Color outline = active ? new Color(180, 150, 255, 240)
                : hover ? new Color(255, 255, 255, 240)
                : new Color(255, 255, 255, 180);
        g.setColor(outline);
        if (g instanceof Graphics2D g2) {
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, bw, bh, 12, 12);
        } else {
            g.drawRoundRect(x, y, bw, bh, 12, 12);
        }
        g.setColor(hover || active ? Color.WHITE : new Color(255, 255, 255, 220));
        int tx = x + (bw - fm.stringWidth(label)) / 2;
        int ty = y + (bh + fm.getAscent()) / 2 - 2;
        g.drawString(label, tx, ty);
    }

    private void drawHowToPlay(Graphics g, int screenW, int screenH) {
        int panelW = Math.min(600, screenW - 80);
        int panelX = (screenW - panelW) / 2;
        int panelY = (int) (screenH * 0.12);
        int lineH = 22;
        int padX = 28, padY = 24;
        int panelH = padY * 2 + HOW_TO_PLAY.length * lineH + 10;

        // Dimmed overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, screenW, screenH);

        // Panel
        g.setColor(new Color(15, 10, 30, 240));
        g.fillRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        g.setColor(new Color(180, 150, 255, 200));
        if (g instanceof Graphics2D g2) {
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(panelX, panelY, panelW, panelH, 16, 16);
        }

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.setColor(Color.WHITE);
        String htpTitle = "How to Play";
        FontMetrics htpFm = g.getFontMetrics();
        g.drawString(htpTitle, panelX + (panelW - htpFm.stringWidth(htpTitle)) / 2, panelY + padY + htpFm.getAscent());

        // Lines
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        int y = panelY + padY + 40;
        for (String line : HOW_TO_PLAY) {
            if (line.isEmpty()) {
                y += lineH / 2;
                continue;
            }
            boolean isHeader = line.equals(line.toUpperCase().trim()) && !line.startsWith(" ");
            if (isHeader) {
                g.setFont(new Font("SansSerif", Font.BOLD, 15));
                g.setColor(new Color(180, 150, 255));
            } else {
                g.setFont(new Font("SansSerif", Font.PLAIN, 15));
                g.setColor(new Color(230, 225, 240));
            }
            g.drawString(line, panelX + padX, y);
            y += lineH;
        }

        // Close hint
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(180, 180, 180, 180));
        String close = "Click \"How to Play\" again or press ESC to close";
        g.drawString(close, panelX + (panelW - g.getFontMetrics().stringWidth(close)) / 2, panelY + panelH - 12);
    }
}

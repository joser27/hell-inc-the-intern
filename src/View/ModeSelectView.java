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
            "One week in town. Meet your quota before Gary pulls you.",
            "Infinite mode. Quotas keep rising. How long can you last?",
            "",
            ""
    };

    private static final String[] HOW_TO_PLAY = {
            "You are a Hell Inc. intern assigned to a small town.",
            "Your job: collect souls by convincing residents to sign deals.",
            "",
            "CONTROLS",
            "  WASD — Move around town",
            "  E — Knock on a door (when near one)",
            "  Type freely — Talk to the NPC in encounters",
            "  ENTER — Send your message",
            "  ESC — Leave encounter / Open options",
            "  Scroll — Read conversation history",
            "",
            "HOW TO WIN",
            "  Reach your soul quota before suspicion hits 100%.",
            "  Each deal raises suspicion. Each rejection risks a tattle.",
            "  Identify what the resident secretly wants, then pitch it.",
            "",
            "HOW TO LOSE",
            "  Suspicion 100% — the town reports you. Assignment over.",
            "  Gary may also pull you if you embarrass Hell Inc.",
            "",
            "TIPS",
            "  Listen carefully — NPCs drop hints about their desires.",
            "  Some NPCs tattle more than others. Plan your approach.",
            "  Revisiting an NPC? They remember your last conversation.",
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

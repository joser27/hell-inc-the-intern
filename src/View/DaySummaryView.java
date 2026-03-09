package View;

import Model.Game;
import Model.gamestates.DaySummary;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the Endless "Assignment Complete" transition: Gary's message, stats, next quota.
 */
public class DaySummaryView {

    private static final int PANEL_W = 640;
    private static final int PANEL_H = 380;
    private static final int PAD = 36;
    private static final int LINE_GAP = 28;

    public void render(Graphics g, DaySummary summary, Game game, int displayW, int displayH) {
        if (summary == null) return;

        g.setColor(new Color(0, 0, 0, 210));
        g.fillRect(0, 0, displayW, displayH);

        int x = (displayW - PANEL_W) / 2;
        int y = (displayH - PANEL_H) / 2;

        g.setColor(new Color(15, 10, 30, 240));
        ((Graphics2D) g).setStroke(new BasicStroke(2f));
        g.fillRoundRect(x, y, PANEL_W, PANEL_H, 16, 16);
        g.setColor(new Color(180, 150, 255, 200));
        g.drawRoundRect(x, y, PANEL_W, PANEL_H, 16, 16);

        int cx = x + PANEL_W / 2;
        int textY = y + PAD;

        String title = "Assignment Complete!";
        g.setFont(new Font("SansSerif", Font.BOLD, 38));
        g.setColor(new Color(230, 200, 255));
        FontMetrics titleFm = g.getFontMetrics();
        g.drawString(title, cx - titleFm.stringWidth(title) / 2, textY + titleFm.getAscent());
        textY += titleFm.getHeight() + 16;

        g.setFont(new Font("SansSerif", Font.ITALIC, 17));
        g.setColor(new Color(255, 255, 200, 230));
        FontMetrics garyFm = g.getFontMetrics();
        int maxTextW = PANEL_W - PAD * 2;
        List<String> garyLines = wrapText(garyFm, summary.getGaryMessage(), maxTextW);
        for (String line : garyLines) {
            g.drawString(line, cx - garyFm.stringWidth(line) / 2, textY);
            textY += garyFm.getHeight() + 2;
        }
        textY += 20;

        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(new Color(200, 195, 220));
        FontMetrics statFm = g.getFontMetrics();

        String[] stats = {
                "Souls this assignment: " + summary.getSnapshotSouls() + " / " + summary.getSnapshotQuota(),
                "Total souls collected: " + summary.getSnapshotTotalSouls(),
                "Suspicion: " + (int) summary.getSnapshotSuspicion() + "%  (-10% relief)",
                "",
                "Next assignment quota: " + summary.getNextQuota() + " souls"
        };

        for (String stat : stats) {
            if (stat.isEmpty()) {
                textY += 8;
                continue;
            }
            if (stat.startsWith("Next assignment")) {
                g.setFont(new Font("SansSerif", Font.BOLD, 18));
                g.setColor(new Color(180, 150, 255));
            }
            g.drawString(stat, cx - statFm.stringWidth(stat) / 2, textY);
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            g.setColor(new Color(200, 195, 220));
            textY += LINE_GAP;
        }

        String hint = "Press ENTER to start next assignment";
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.setColor(new Color(180, 180, 180, 200));
        FontMetrics hintFm = g.getFontMetrics();
        g.drawString(hint, cx - hintFm.stringWidth(hint) / 2, y + PANEL_H - PAD + 4);
    }

    private static List<String> wrapText(FontMetrics fm, String text, int maxWidth) {
        List<String> out = new ArrayList<>();
        if (text == null || text.isEmpty()) return out;
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String trial = current.length() == 0 ? word : current + " " + word;
            if (fm.stringWidth(trial) <= maxWidth) {
                current.setLength(0);
                current.append(trial);
            } else {
                if (current.length() > 0) out.add(current.toString());
                current.setLength(0);
                current.append(word);
            }
        }
        if (current.length() > 0) out.add(current.toString());
        return out;
    }
}

package View;

import Controller.GameController;
import Model.gamestates.Loading;

import java.awt.*;

/**
 * Full-screen loading: black background, centered spinner, "Loading", and one random flavor line.
 */
public class LoadingView {
    private static final int SPINNER_RADIUS = 36;
    private static final int SPINNER_STROKE = 6;
    private static final int TITLE_FONT_SIZE = 32;
    private static final int FLAVOR_FONT_SIZE = 16;
    private static final int GAP_TITLE_TO_SPINNER = 24;
    private static final int GAP_SPINNER_TO_FLAVOR = 48;

    /** Use displayWidth/displayHeight so fullscreen fills the entire panel (no bar at bottom). */
    public void render(Graphics g, Loading loading, int displayWidth, int displayHeight) {
        if (loading == null) return;
        int w = displayWidth > 0 ? displayWidth : GameController.GAME_WIDTH;
        int h = displayHeight > 0 ? displayHeight : GameController.GAME_HEIGHT;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);

        int centerX = w / 2;
        int centerY = h / 2;

        // "Loading" text above spinner
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, TITLE_FONT_SIZE));
        FontMetrics titleFm = g.getFontMetrics();
        String loadingText = "Loading";
        int titleY = centerY - SPINNER_RADIUS - GAP_TITLE_TO_SPINNER - titleFm.getHeight() / 2;
        g.drawString(loadingText, centerX - titleFm.stringWidth(loadingText) / 2, titleY + titleFm.getAscent());

        // Rotating wheel (arc that spins)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int wheelX = centerX - SPINNER_RADIUS;
        int wheelY = centerY - SPINNER_RADIUS;
        long time = System.currentTimeMillis();
        int startAngle = (int) ((time / 50) % 360);
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(SPINNER_STROKE, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(wheelX, wheelY, SPINNER_RADIUS * 2, SPINNER_RADIUS * 2, startAngle, 270);
        g2.dispose();

        // Flavor text at bottom
        String line = loading.getFlavorLine();
        if (line != null && !line.isEmpty()) {
            g.setColor(new Color(140, 140, 140));
            g.setFont(new Font("SansSerif", Font.ITALIC, FLAVOR_FONT_SIZE));
            FontMetrics flavorFm = g.getFontMetrics();
            int flavorY = centerY + SPINNER_RADIUS + GAP_SPINNER_TO_FLAVOR + flavorFm.getAscent();
            int lineW = flavorFm.stringWidth(line);
            if (lineW > w - 80) {
                g.drawString(line, 40, flavorY);
            } else {
                g.drawString(line, centerX - lineW / 2, flavorY);
            }
        }
    }
}

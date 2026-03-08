package View;

import Controller.GameController;
import Controller.GameLoop;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    GameLoop gameLoop;
    GameController gameController;

    /** Actual FPS: count of completed paints in the last second (EDT), not repaint() requests. */
    private volatile int lastFps;
    private int framesThisSecond;
    private long lastFpsTimeMs;

    public GamePanel(GameController gameController) {
        this.gameController = gameController;
        lastFpsTimeMs = System.currentTimeMillis();
        setPanelSize();
    }

    public void setPanelSize() {
        Dimension dimension = new Dimension(gameController.getScreenWidth(), gameController.getScreenHeight());
        setPreferredSize(dimension);
        setLayout(null);
        setBackground(new Color( 79,131,52,255));
        setDoubleBuffered(true);
        setFocusable(true);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameLoop.render(g);
        long now = System.currentTimeMillis();
        framesThisSecond++;
        if (now - lastFpsTimeMs >= 1000) {
            lastFps = framesThisSecond;
            framesThisSecond = 0;
            lastFpsTimeMs = now;
        }
    }

    /** Frames actually drawn in the last completed second (read from game thread). */
    public int getLastFps() {
        return lastFps;
    }

    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }
}

package View;

import Model.utilz.LoadSave;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GameFrame {

    JFrame frame;
    GamePanel gamePanel;
    private boolean fullscreen = false;
    private final Rectangle windowedBounds;
    private Image windowIcon;

    public GameFrame(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        windowedBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        loadWindowIcon();
        initFrame();
    }

    private void loadWindowIcon() {
        try {
            BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.HEADER_LOGO);
            if (img != null) windowIcon = img;
        } catch (Exception ignored) {
            windowIcon = null;
        }
    }

    private void applyIconToFrame(JFrame f) {
        if (windowIcon != null) f.setIconImage(windowIcon);
    }

    public void initFrame() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.add(gamePanel);
        frame.setSize(windowedBounds.width, windowedBounds.height);
        frame.setLocation(windowedBounds.x, windowedBounds.y);
        frame.setUndecorated(false);
        frame.setResizable(true);
        applyIconToFrame(frame);
        frame.setVisible(true);
    }

    public boolean isFullscreen() { return fullscreen; }

    /**
     * True fullscreen = borderless and maximized. Java does not allow setUndecorated()
     * after the frame is shown, so we recreate the frame when toggling.
     */
    public void setFullscreen(boolean fullscreen) {
        if (this.fullscreen == fullscreen) return;
        this.fullscreen = fullscreen;

        frame.setVisible(false);
        frame.remove(gamePanel);
        frame.dispose();

        frame = new JFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setUndecorated(fullscreen);
        frame.add(gamePanel);
        applyIconToFrame(frame);
        if (fullscreen) {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setResizable(false);
        } else {
            frame.setSize(windowedBounds.width, windowedBounds.height);
            frame.setLocation(windowedBounds.x, windowedBounds.y);
            frame.setResizable(true);
        }
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }

    /** Close the game (triggers default close operation → EXIT_ON_CLOSE). */
    public void quit() {
        if (frame != null) {
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        }
    }
}

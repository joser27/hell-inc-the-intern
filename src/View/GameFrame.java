package View;

import javax.swing.*;
import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class GameFrame {

    JFrame frame;
    GamePanel gamePanel;

    public GameFrame(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        initFrame();
    }

    public void initFrame() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.add(gamePanel);

        // Use usable screen area (excludes taskbar) so dialogue and UI are not clipped
        Rectangle usable = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        frame.setSize(usable.width, usable.height);
        frame.setLocation(usable.x, usable.y);
        frame.setUndecorated(false);  // set true for borderless fullscreen
        frame.setResizable(true);
        frame.setVisible(true);
    }
}

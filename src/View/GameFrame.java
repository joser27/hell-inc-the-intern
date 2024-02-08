package View;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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

        // Make the frame full screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true); // Optional: removes window decorations

        frame.setVisible(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }
}

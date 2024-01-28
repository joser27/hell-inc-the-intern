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
        frame.setVisible(true);
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }
    public void gameOverScreen(){
        JFrame gameOver = new JFrame();
        gameOver.setDefaultCloseOperation(EXIT_ON_CLOSE);
        gameOver.setVisible(true);

        JPanel overPanel = new JPanel();
        overPanel.setPreferredSize(new Dimension(400,400));
        overPanel.setLayout(null);
        overPanel.setBackground(new Color( 135, 206, 235));
        overPanel.setDoubleBuffered(true);
        overPanel.setFocusable(true);
        gameOver.add(overPanel);
        try {
            BufferedImage myPicture = ImageIO.read(new File("gameOverImg.jpg"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            gameOver.add(picLabel);
        } catch (IOException e) {
            System.err.println("No image file");
        }

        frame.setVisible(false);


        gameOver.pack();
        gameOver.setLocationRelativeTo(null);

    }
}

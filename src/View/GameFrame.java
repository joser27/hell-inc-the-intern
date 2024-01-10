package View;

import javax.swing.*;

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
}

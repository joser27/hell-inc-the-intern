package View;

import Controller.GameController;
import Controller.GameLoop;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    GameLoop gameLoop;
    GameController gameController;
    public GamePanel(GameController gameController) {
        this.gameController = gameController;
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameLoop.render(g);
    }

    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }
}

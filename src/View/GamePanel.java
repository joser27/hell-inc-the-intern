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
        setBackground(new Color( 135, 206, 235));
        setDoubleBuffered(true);
        setFocusable(true);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

//        // Draw vertical lines
//        for (int x = 0; x <= gameController.getScreenWidth(); x += gameController.getTileSize()) {
//            g.drawLine(x, 0, x, gameController.getScreenHeight());
//        }
//
//        // Draw horizontal lines
//        for (int y = 0; y <= gameController.getScreenHeight(); y += gameController.getTileSize()) {
//            g.drawLine(0, y, gameController.getScreenWidth(), y);
//        }


        gameLoop.render(g);
    }

    public void setGameLoop(GameLoop gameLoop) {
        this.gameLoop = gameLoop;
    }
}

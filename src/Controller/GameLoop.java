package Controller;

import Model.Game;
import View.GameFrame;
import View.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameLoop{

//    private static final int WIDTH = 800;
//    private static final int HEIGHT = 600;

    private boolean running;
    private int x, y;
    private int frameCount;
    private long startTime;
    GamePanel gamePanel;
    Game game;
    GameController controller;
    GameFrame gameFrame;
    public GameLoop(Game game, GamePanel gamePanel,GameController controller) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.controller = controller;



        running = true;
        frameCount = 0;
        startTime = System.currentTimeMillis();

        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                gamePanel.repaint();
                frameCount++;
            }
        });
        timer.start();

        Timer fpsTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateFPS();
            }
        });
        fpsTimer.start();
    }

    private void calculateFPS() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        if (elapsedTime > 0) {
            double fps = (double) frameCount / (elapsedTime / 1000.0);
            System.out.println("FPS: " + Math.round(fps));
        }

        frameCount = 0;
        startTime = currentTime;
    }

    private void update() {
        controller.update();
    }

    public void render(Graphics g) {
        controller.render(g);
    }
}

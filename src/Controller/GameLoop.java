package Controller;
import Model.Game;

import View.GamePanel;

import java.awt.*;

public class GameLoop implements Runnable {
    private Game model;
    private GamePanel view;
    private GameController controller;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 120;
    private final long NANO_PER_SEC = 1_000_000_000L;

    private volatile boolean isRunning = true;

    public GameLoop(Game model, GamePanel view, GameController controller) {
        this.model = model;
        this.view = view;
        this.controller = controller;
    }

    public void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGameLoop() {
        isRunning = false;
    }
    public void update() {
        controller.update();
    }
    public void render(Graphics g) {
        controller.render(g);
    }


    @Override
    public void run() {
        double timePerFrame = NANO_PER_SEC / FPS_SET;
        double timePerUpdate = NANO_PER_SEC / UPS_SET;

        long previousTime = System.nanoTime();
        long currentTime;
        long lastCheck = System.currentTimeMillis();

        int frames = 0;
        int updates = 0;

        double deltaU = 0;
        double deltaF = 0;

        while (isRunning) {
            currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                view.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
    }
//    public Game getModel() {
//        return model;
//    }
//
//    public GamePanel getView() {
//        return view;
//    }
}

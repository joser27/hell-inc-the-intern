package View;

import Model.gamestates.PauseMenu;

import java.awt.*;

public class PauseMenuView {
    public void render(Graphics g, PauseMenu pauseMenu) {
        if (pauseMenu == null) return;
        int posX = pauseMenu.getPosX();
        int posY = pauseMenu.getPosY();
        java.awt.image.BufferedImage[][] img = pauseMenu.getPauseImages();
        g.setColor(new Color(127, 20, 20, 200));
        g.drawImage(img[0][0], posX, posY, null);
        g.drawImage(img[1][0], posX + img[0][0].getWidth(), posY, null);
        g.drawImage(img[2][0], posX + img[0][0].getWidth() * 2, posY, null);
        g.drawImage(img[0][1], posX, posY + img[0][0].getHeight(), null);
        g.drawImage(img[1][1], posX + img[0][0].getWidth(), posY + img[0][0].getHeight(), null);
        g.drawImage(img[2][1], posX + img[0][0].getWidth() * 2, posY + img[0][0].getHeight(), null);
        g.drawImage(img[0][2], posX, posY + img[0][0].getHeight() * 2, null);
        g.drawImage(img[1][2], posX + img[0][0].getWidth(), posY + img[0][0].getHeight() * 2, null);
        g.drawImage(img[2][2], posX + img[0][0].getWidth() * 2, posY + img[0][0].getHeight() * 2, null);

        Font font = pauseMenu.getPauseFont();
        g.setFont(font);
        g.setColor(pauseMenu.getResumeColor());
        g.drawString("Resume", (int) (posX * 1.51), (int) (posY * 4.8));
        g.setFont(font);
        g.setColor(pauseMenu.getExitColor());
        g.drawString("Exit", (int) (posX * 1.56), (int) (posY * 5.3));
    }
}

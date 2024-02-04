package Model.gamestates;

import Controller.GameController;
import Model.Game;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class LoadMenu extends State implements Statemethods {
    private BufferedImage startButtonImg;
    private Image scaledImg;
    private int x , y;
    private int startPlacementX = 500, startPlacementY = 500;
    private int startButtonWidth = 300, startButtonHeight = 280;


    public LoadMenu(Game game) {
        super(game);
        startButtonImg = LoadSave.GetSpriteAtlas(LoadSave.START_BUTTON);
        scaledImg = startButtonImg.getScaledInstance(startButtonWidth,startButtonHeight,Image.SCALE_DEFAULT);
    }

    @Override
    public void update() {
        if (x > startPlacementX && x < startPlacementX + startButtonWidth) {
            if (y > startPlacementY && y < startPlacementY + startButtonHeight ) {
                x = 0;
                y = 0;
                Gamestate.state = Gamestate.PLAYING;
            }
        }

    }

    @Override
    public void render(Graphics g) {
        // Draw the image at a specified position

        g.drawImage(scaledImg, startPlacementX, startPlacementY, null);


        // Draw the black text
        g.setColor(Color.BLACK);
        g.drawString("MENU", GameController.GAME_WIDTH / 2, 300);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ENTER) {
            Gamestate.state = Gamestate.PLAYING;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

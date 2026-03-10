package Model.gamestates;

import Controller.GameController;
import Model.Game;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PauseMenu extends State implements Statemethods {

    private int x,y;
    private int hovX, hovY;
    private int posX, posY;
    private BufferedImage[][] img;
    private Color exitColor = Color.BLACK;
    private Color resumeColor = Color.BLACK;
    private int gameWidth = GameController.GAME_WIDTH;
    private int gameHeight = GameController.GAME_HEIGHT;
    private int pauseSizeWidth = (int) (gameWidth/7.5);
    private int pauseSizeHeight = (int) (gameHeight/3.8);

    public PauseMenu(Game game) {
        super(game);
        img = new BufferedImage[20][9];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 9; j++) {
                BufferedImage tile = new BufferedImage(pauseSizeWidth, pauseSizeHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics g = tile.getGraphics();
                g.setColor(new Color(60, 50, 80, 220));
                g.fillRect(0, 0, pauseSizeWidth, pauseSizeHeight);
                g.setColor(new Color(100, 85, 130, 200));
                g.drawRect(0, 0, pauseSizeWidth - 1, pauseSizeHeight - 1);
                g.dispose();
                img[i][j] = tile;
            }
        }
    }

    @Override
    public void update() {
        posX = getPosX();
        posY = getPosY();
        if (x > (posX*1.5) && x < posX + img[0][0].getWidth()*1.8) {
            if (y > (posY*4.6) && y < posY + img[0][0].getHeight()*1.8) {
                x=0;
                y=0;
                Gamestate.state = Gamestate.PLAYING;
            }
        }

        if (x > (posX*1.5) && x < posX + img[0][0].getWidth()*1.8) {
            if (y > (posY*5.1) && y < posY + img[0][0].getHeight()*2.1) {
                x=0;
                y=0;
                System.exit(0);
            }
        }


        if (hovX > (posX*1.5) && hovX < posX + img[0][0].getWidth()*1.8) {
            if (hovY > (posY*4.6) && hovY < posY + img[0][0].getHeight()*1.8) {
                resumeColor = Color.YELLOW;
            }
            else {
                resumeColor = Color.BLACK;
            }
        }

        if (hovX > (posX*1.5) && hovX < posX + img[0][0].getWidth()*1.8) {
            if (hovY > (posY*5.1) && hovY < posY + img[0][0].getHeight()*2.1) {
                exitColor = Color.RED;
            }
            else {
                exitColor = Color.BLACK;
            }
        }
    }

    @Override
    public void render(Graphics g) { /* Rendering done by View.PauseMenuView */ }

    public int getPosX() {
        return (int) (((gameWidth - gameWidth / 2) / 2) + img[0][0].getWidth()/2.5);
    }
    public int getPosY() {
        return (int) (((gameHeight - gameHeight / 2) / 2) - img[0][0].getHeight()/2);
    }
    public BufferedImage[][] getPauseImages() { return img; }
    public Color getResumeColor() { return resumeColor; }
    public Color getExitColor() { return exitColor; }
    public Font getPauseFont() { return LoadSave.GetFont(LoadSave.FONT_DEFAULT, 26); }

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
        hovX = e.getX();
        hovY = e.getY();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE) {
            Gamestate.state = Gamestate.PLAYING;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

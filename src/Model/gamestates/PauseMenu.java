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
    Color exitColor = Color.BLACK;
    Color resumeColor = Color.BLACK;
    public PauseMenu(Game game) {
        super(game);
        img = new BufferedImage[20][9];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.UI_SQUARES);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 9; j++) {
                Image scaledImage = temp.getSubimage((320/20) * i, (144/9) * j, 320/20, 144/9).getScaledInstance(64 * GameController.SCALE, 70 * GameController.SCALE, Image.SCALE_DEFAULT);

                // Convert Image to BufferedImage
                BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bufferedImage.getGraphics();
                g.drawImage(scaledImage, 0, 0, null);
                g.dispose();

                img[i][j] = bufferedImage;
            }
        }
    }

    @Override
    public void update() {
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
    public void render(Graphics g) {
        g.setColor(new Color(127, 20, 20,200));
        posX = (int) (((GameController.GAME_WIDTH - GameController.GAME_WIDTH / 2) / 2) + img[0][0].getWidth()/2.5);
        posY = (int) (((GameController.GAME_HEIGHT - GameController.GAME_HEIGHT / 2) / 2) - img[0][0].getHeight()/2);
        g.drawImage(img[0][0],posX,posY,null);                               g.drawImage(img[1][0],posX+img[0][0].getWidth(),posY,null);                              g.drawImage(img[2][0],posX+img[0][0].getWidth()*2,posY,null);
        g.drawImage(img[0][1],posX,posY+img[0][0].getHeight(),null);      g.drawImage(img[1][1],posX+img[0][0].getWidth(),posY+img[0][0].getHeight(),null);     g.drawImage(img[2][1],posX+img[0][0].getWidth()*2,posY+img[0][0].getHeight(),null);
        g.drawImage(img[0][2],posX,posY+img[0][0].getHeight()*2,null);    g.drawImage(img[1][2],posX+img[0][0].getWidth(),posY+img[0][0].getHeight()*2,null);   g.drawImage(img[2][2],posX+img[0][0].getWidth()*2,posY+img[0][0].getHeight()*2,null);

        Font font = LoadSave.GetFont(LoadSave.FONT_DEFAULT,26);

        g.setFont(font);
        g.setColor(resumeColor);
        g.drawString("Resume", (int) (posX*1.51), (int) (posY*4.8));

        g.setFont(font);
        g.setColor(exitColor);
        g.drawString("Exit", (int) (posX*1.56), (int) (posY*5.3));
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

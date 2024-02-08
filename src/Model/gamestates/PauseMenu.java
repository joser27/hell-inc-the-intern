package Model.gamestates;

import Controller.GameController;
import Model.Game;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class PauseMenu extends State implements Statemethods {

    BufferedImage[][] img;
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

    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(127, 20, 20,200));
        int posX = (int) (((GameController.GAME_WIDTH - GameController.GAME_WIDTH / 2) / 2) + img[0][0].getWidth()/2.5);
        int posY = (int) (((GameController.GAME_HEIGHT - GameController.GAME_HEIGHT / 2) / 2) - img[0][0].getHeight()/2);
        g.drawImage(img[0][0],posX,posY,null);                               g.drawImage(img[1][0],posX+img[0][0].getWidth(),posY,null);                              g.drawImage(img[2][0],posX+img[0][0].getWidth()*2,posY,null);
        g.drawImage(img[0][1],posX,posY+img[0][0].getHeight(),null);      g.drawImage(img[1][1],posX+img[0][0].getWidth(),posY+img[0][0].getHeight(),null);     g.drawImage(img[2][1],posX+img[0][0].getWidth()*2,posY+img[0][0].getHeight(),null);
        g.drawImage(img[0][2],posX,posY+img[0][0].getHeight()*2,null);    g.drawImage(img[1][2],posX+img[0][0].getWidth(),posY+img[0][0].getHeight()*2,null);   g.drawImage(img[2][2],posX+img[0][0].getWidth()*2,posY+img[0][0].getHeight()*2,null);

        Font font = LoadSave.GetFont(LoadSave.FONT_MINECRAFT,26);
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString("Exit This is my Font", (int) (posX*1.4), (int) (posY*3.5));

        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString("Exit This is my other Font", (int) (posX*1.4), (int) (posY*3.8));
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

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

package Model;

import Controller.GameController;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends Entity {

    Image img;

    public Wall(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);

        img = LoadSave.GetSpriteAtlas(LoadSave.TREE_4).getScaledInstance((int) getHitBox().width + 11 * GameController.SCALE, (int) getHitBox().height+16 * GameController.SCALE,Image.SCALE_DEFAULT);
    }

    public void update(int xLvlOffset) {
        getHitBox().x += xLvlOffset;
    }




    public void render(Graphics g,int xLvlOffset, int yLvlOffset) {
        g.drawImage(img,(int) getHitBox().x-18 - xLvlOffset, (int) getHitBox().y-48 - yLvlOffset,null);
//        g.fillRect((int) getHitBox().x-xLvlOffset, (int) getHitBox().y, (int) getHitBox().width, (int) getHitBox().height);
    }

}

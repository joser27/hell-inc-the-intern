package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Wall extends Entity {

    Image img;

    public Wall(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);

        img = LoadSave.GetSpriteAtlas(LoadSave.TREE_4).getScaledInstance((int) getHitBox().width + 35, (int) getHitBox().height+50,Image.SCALE_DEFAULT);
    }

    public void update(int xLvlOffset) {
        getHitBox().x += xLvlOffset;
    }




    public void render(Graphics g,int xLvlOffset) {
//        g.drawImage(img,(int) getHitBox().x-18 - xLvlOffset, (int) getHitBox().y-48,null);
        g.fillRect((int) getHitBox().x-xLvlOffset, (int) getHitBox().y, (int) getHitBox().width, (int) getHitBox().height);
    }

}

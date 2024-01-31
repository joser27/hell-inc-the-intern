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



    @Override
    public void render(Graphics g) {
        g.drawImage(img,(int) getHitBox().x-18, (int) getHitBox().y-48,null);
//        g.drawRect((int) getHitBox().x, (int) getHitBox().y, (int) getHitBox().width, (int) getHitBox().height);
    }

}

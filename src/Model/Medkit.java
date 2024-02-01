package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Medkit implements Item {
    int xPos, yPos;
    BufferedImage img;
    Rectangle2D.Float hitBox;
    private int size = 25;
    public Medkit(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        img = LoadSave.GetSpriteAtlas(LoadSave.MEDKIT_TILE);
        hitBox = new Rectangle2D.Float(xPos,yPos,size,size);
    }
    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(img.getScaledInstance((int) hitBox.width, (int) hitBox.height,Image.SCALE_DEFAULT),xPos,yPos,null);
        g.drawRect((int) hitBox.x, (int) hitBox.y, (int) hitBox.width, (int) hitBox.height);
    }
}

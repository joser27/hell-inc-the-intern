package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Medkit implements Item {
    private int xPos, yPos;
    private Image img;
    private Rectangle2D.Float hitBox;
    private int width = 16;
    private int height = 16;
    private boolean active;
    public Medkit(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        hitBox = new Rectangle2D.Float(xPos,yPos,width,height);
        img = LoadSave.GetSpriteAtlas(LoadSave.MEDKIT_TILE).getScaledInstance((int) hitBox.width+13, (int) hitBox.height+19,Image.SCALE_DEFAULT);

    }
    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {
        g.drawImage(img,xPos-5,yPos-12,null);

            g.drawRect((int) hitBox.x, (int) hitBox.y, (int) hitBox.width, (int) hitBox.height);
    }

    public Rectangle2D.Float getHitBox() {
        return hitBox;
    }

    public void setHitBox(Rectangle2D.Float hitBox) {
        this.hitBox = hitBox;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

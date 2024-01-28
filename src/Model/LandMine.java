package Model;

import java.awt.*;

public class LandMine {

    private Rectangle landMineHitBox;
    private int landMineSize = 10;
    private boolean exploded = false;
    public LandMine(int xPos, int yPos, int width, int height) {
        landMineHitBox = new Rectangle(xPos,yPos,width,height);
    }


    public void explode() {
        exploded = true;
        landMineHitBox.width = 48;
        landMineHitBox.height = 48;
        landMineHitBox.x = (getLandMineHitBox().x/48) * 48;
        landMineHitBox.y = (getLandMineHitBox().y/48) * 48;

    }
    public void render(Graphics g) {
        g.fillRect(landMineHitBox.x, landMineHitBox.y, landMineHitBox.width, landMineHitBox.height);
    }


    public Rectangle getLandMineHitBox() {
        return landMineHitBox;
    }
}

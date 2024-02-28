package Model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Smash extends Ability {
     public Rectangle2D.Float attackSmashHitBox;
    public boolean attackingSmash  = false;
    public int attackingSmashCD = 20;
    public int attackingSmashTimer;
    public int attackingSmashDuration;
    public boolean canSmashAttack = true;
    public Smash(int scale,int xPos, int yPos) {
        super(scale,xPos,yPos);
        attackSmashHitBox = new Rectangle2D.Float(xPos,yPos,scale,scale);

    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.yellow);
        g.fillRect((int) attackSmashHitBox.x-xLvlOffset, (int) attackSmashHitBox.y-yLvlOffset, scale,scale);
    }
}

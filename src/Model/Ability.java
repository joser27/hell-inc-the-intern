package Model;

import java.awt.*;

public abstract class Ability {
    int scale;

    public Ability(int scale, int xPos, int yPos) {
        this.scale = scale;
    }

    public abstract void update();

    public abstract void render(Graphics g, int xLvlOffset, int yLvlOffset);
}

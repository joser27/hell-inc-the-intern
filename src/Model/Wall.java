package Model;

import java.awt.*;

public class Wall extends Entity{

    public Wall(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
    }

    @Override
    public void render(Graphics g) {
        g.fillRect((int) getHitBox().x, (int) getHitBox().y, (int) getHitBox().width, (int) getHitBox().height);
    }
}

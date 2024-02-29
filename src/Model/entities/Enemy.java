package Model.entities;

import Model.Game;
import Model.entities.Entity;

import java.awt.*;

public class Enemy extends Entity {

    private Game game;
    private int gameFreezeFrames = 30;
    private int myX, myY;

    public Enemy(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos,yPos,width,height,movementSpeed,game);
        this.game = game;



    }

    public void update() {

    }


    public void render(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);

        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Bot coords: " + myX/48 + " " + myY/48, 100, 200);

    }



    @Override
    public String toString() {
        return "ENEMY";
    }
}

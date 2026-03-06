package Model.entities;

import Model.Game;

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


    public int getMyX() { return myX; }
    public int getMyY() { return myY; }

    @Override
    public String toString() {
        return "ENEMY";
    }
}

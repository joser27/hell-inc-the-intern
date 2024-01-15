package Model;

import java.awt.*;



public abstract class Player extends Entity {
    Game game;
    int playerX;
    int playerY;
    public Player(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos,yPos,width,height,movementSpeed,game);
        this.game = game;

    }

    public void update() {

        updatePos();
        int y = (int) getHitBox().y;
        int x = (int) getHitBox().x;


        for (int i = 0; i < LevelLoader.world.length; i++) {
            for (int j = 0; j < LevelLoader.world[0].length; j++) {
                if (y / 48 == i && x / 48 == j) {
                    playerX = i;
                    playerY = j;
                }
            }
        }
    }

    public abstract void updatePos();


    public abstract void render(Graphics g);
    @Override
    public String toString() {
        return "PLAYER";
    }
}

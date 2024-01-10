package Model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Enemy extends Entity{

    private Game game;
    private int gameFreezeFrames = 30;
    private int prevX, prevY;
    public Enemy(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos,yPos,width,height,movementSpeed,game);

        this.game = game;
    }

    public void update() {
        gameFreezeFrames++;
//        System.out.println(gameFreezeFrames);

        float xSpeed = 0f;
        float ySpeed = 0f;
        prevX = getxPos();
        prevY = getyPos();

        Rectangle2D.Float currHitBox = getHitBox();
        Rectangle2D.Float playerHitBox = game.getPlayer().getHitBox();
        int playerX = (int) playerHitBox.x;
        int playerY = (int) playerHitBox.y;

        if (playerX > currHitBox.x) {
            xSpeed += getMovementSpeed();
        } else if (playerX < currHitBox.x) {
            xSpeed -= getMovementSpeed();
        }

        if (playerY > currHitBox.y) {
            ySpeed += getMovementSpeed();
        } else if (playerY < currHitBox.y) {
            ySpeed -= getMovementSpeed();
        }


        // CHECK COLLISION BETWEEN ENTITIES
        game.getCollisionChecker().handleCollision(this, game.getEntities(), xSpeed, ySpeed);

//        if (prevX == getxPos() || prevY == getyPos()) {
//            System.err.println("Enemy is stuck");
//            if (xSpeed == Math.abs(getMovementSpeed())) {//Positive
//                System.err.println("Enemy is moving right");
//
//            }
//             else if (xSpeed == getMovementSpeed() * -1) {//Negative
//                System.err.println("Enemy is moving left");
//            }
//        }
    }

    @Override
    public String toString() {
        return "ENEMY";
    }
}

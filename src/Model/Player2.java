package Model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Player2 extends Player{

    Rectangle bullet;
    int bulletCount = 4;
    public Player2(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
    }


    @Override
    public void updatePos() {
        int xSpeed = 0;
        int ySpeed = 0;
        if (isLeft()) {
            xSpeed -= getMovementSpeed();
        }
        if (isRight()) {
            xSpeed += getMovementSpeed();
        }
        if (isDown()) {
            ySpeed += getMovementSpeed();

        }
        if (isUp()) {
            ySpeed -= getMovementSpeed();
        }
        game.getCollisionChecker().handleCollision(this, game.getEntities(),xSpeed,ySpeed);
    }
    public void shoot() {
        if (bulletCount > 0 ) {
            bulletCount--;
            bullet = new Rectangle(getxPos(),getyPos(),10,10);
        }
    }
    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);


        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Player2 coords: " + playerY + " " + playerX, 100, 150);
        if (bullet != null) {
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }
//        g.setColor(Color.BLACK);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
    }
    public Rectangle getBullet() {
        return bullet;
    }
}

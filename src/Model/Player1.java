package Model;

import java.awt.*;

/*
 * The Tagger
 */
public class Player1 extends Player{

    private int speedBoostLimit = 0;
    private int speedBoostUsages = 4;
    private boolean speedBoostOn = false;
    float prevMS = getMovementSpeed();
    public Player1(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
    }
    public void respawn() {
        setXHitBox(50);
        setYHitBox(50);
    }
    public void speedBoost() {
        speedBoostOn = true;

    }
    public void update() {
        updatePos();

        if (speedBoostOn && speedBoostUsages>0) {
            speedBoostLimit++;
            setMovementSpeed(3f);
            if (speedBoostLimit >= 10) {
                speedBoostUsages--;
                speedBoostOn = false;
                speedBoostLimit = 0;
                setMovementSpeed(prevMS);
            }
        }

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


    @Override
    public void render(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);


        Font font = new Font("Arial", Font.BOLD, 20);
        g.setFont(font);
        g.drawString("Player1 coords: " + playerY + " " + playerX + ", Boosts: " + speedBoostUsages, 50, 100);

        g.setColor(Color.WHITE);
        g.drawString("YELLOW PLAYER TAG, RED PLAYER RUN ", 50, 40);


//        g.setColor(Color.BLACK);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
    }


}

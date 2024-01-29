package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

/*
 * The Tagger
 */
public class Player1 extends Player{

    private int speedBoostLimit = 0;
    private int speedBoostUsages = 40;
    private boolean speedBoostOn = false;
    float prevMS = getMovementSpeed();
    boolean godMode = false;
    public Player1(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
        setBufferedImage(LoadSave.GetSpriteAtlas(LoadSave.PLAYER1_IMG));

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
            godMode = true;
            speedBoostLimit++;
            setMovementSpeed(3f);

            if (speedBoostLimit >= 20) {
                godMode = false;
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
//        g.setColor(Color.YELLOW);
//        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);
        Image scaledImg = getBufferedImage().getScaledInstance(getWidth()+40,getHeight(),Image.SCALE_DEFAULT);
        g.drawImage(scaledImg,getxPos()-20, getyPos(),null);


        Font font = new Font("Arial", Font.BOLD, 18);
        g.setFont(font);
        g.drawString("Player1 coords: " + getyPos()/48 + " " + getxPos()/48 + ", Boosts: " + speedBoostUsages + "; HP:" + getHealth(), 50, 100);

        g.setColor(Color.WHITE);
        g.drawString("YELLOW PLAYER TAG, RED PLAYER RUN ", 50, 40);


//        g.setColor(Color.BLACK);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
    }

    public boolean isGodMode() {
        return godMode;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }
}

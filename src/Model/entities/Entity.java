package Model.entities;

import Model.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static Model.utilz.Constants.PlayerConstants.*;

public abstract class Entity {
    private boolean left, right,up,down;
    private int yPos, xPos;
    private int width, height;
    private float movementSpeed;
    private final float baseMovementSpeed;
    private Rectangle2D.Float hitBox;
    private Game game;
    private int health = 100;

    //0 = right, 1 = left, 2 = up, 3 = down
    private int facingDir = 0;
    private BufferedImage bufferedImage;
    int[] action;
    protected int aniTick, aniIndex, aniSpeed = 20;
    protected int aniTickSmash, aniIndexSmash, aniSpeedSmash = 110;
    protected int actionOffset;
    protected int animationCol, animationRow, animationFrames;
    public String playerAction = RUNNING_DOWN;
    protected String lastPlayerAction = "";
    protected boolean isMoving = false;

    public Entity(int xPos,  int yPos, int width, int height, float movementSpeed,  Game game) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
        this.movementSpeed = movementSpeed;
        this.game = game;
        baseMovementSpeed = movementSpeed;
        initHitBox();
    }

    public void initHitBox() {
        hitBox = new Rectangle2D.Float(xPos, yPos,width,height);
    }
    public void drawHitBox(Graphics g) {
        g.setColor(Color.PINK);
        g.fillRect((int) hitBox.x, (int) hitBox.y, (int) hitBox.width, (int) hitBox.height);
    }
    protected void updateAnimationTick() {
        action = GetSpriteAmountColRow(playerAction);//COL,ROW,ANIMATION LENGTH
        if (playerAction.equals(IDLE)) {
            if (getFacingDir() == 0) {//0 = right, 1 = left, 2 = up, 3 = down
                action[0] = 0;
                action[1] = 2;
            }
            if (getFacingDir() == 1) {//0 = right, 1 = left, 2 = up, 3 = down
                action[0] = 0;
                action[1] = 6;
            }
            if (getFacingDir() == 2) {//0 = right, 1 = left, 2 = up, 3 = down
                action[0] = 0;
                action[1] = 4;
            }
            if (getFacingDir() == 3) {//0 = right, 1 = left, 2 = up, 3 = down
                action[0] = 0;
                action[1] = 0;
            }
        }

        if (!playerAction.equals(lastPlayerAction)) {// Animation action has changed, reset animation index
            aniIndex = 0;
            lastPlayerAction = playerAction;
        }

        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            animationCol = action[0];
            animationRow = action[1];
            animationFrames = action[2];
            if (aniIndex >= animationFrames) {
                aniIndex = actionOffset;
            }
        }

    }
    public void update() {



        float xSpeed = 0f;
        float ySpeed = 0f;

        // CHECK COLLISION BETWEEN ENTITIES
        game.getCollisionChecker().handleCollision(this, game.getEntities(), xSpeed, ySpeed);

    }

    public void updateEntityPos(float xSpeed, float ySpeed) {
        setxPos(xSpeed);
        setyPos(ySpeed);
    }
    public void render(Graphics g) {
        drawHitBox(g);
    }

    public float getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(float movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public float getBaseMovementSpeed() {
        return baseMovementSpeed;
    }

    public int getyPos() {
        return (int) hitBox.y;
    }

    public void setyPos(float yPos) {
        this.hitBox.y += yPos;
    }

    public int getxPos() {
        return (int) hitBox.x;
    }

    public void setxPos(float xPos) {
        this.hitBox.x += xPos;
    }

    public void setXHitBox(int x) {
        hitBox.x = x;
    }
    public void setYHitBox(int y) {
        hitBox.y = y;
    }
    public Rectangle2D.Float getHitBox() {
        return hitBox;
    }

    public void setHitBox(Rectangle2D.Float hitBox) {
        this.hitBox = hitBox;
    }
    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getFacingDir() {//0 = right, 1 = left, 2 = up, 3 = down
        return facingDir;
    }

    public void setFacingDir(int facingDir) {
        this.facingDir = facingDir;
    }

    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public void incrementHealth(int health) {
        this.health += health;
    }
    public void decrementHealth(int health) {
        this.health += health;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
}

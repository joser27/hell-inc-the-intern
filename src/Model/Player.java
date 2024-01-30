package Model;

import java.awt.*;

import static Model.utilz.Constants.PlayerConstants.*;


public abstract class Player extends Entity {
    Game game;
    int playerX;
    int playerY;
    public Player(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos,yPos,width,height,movementSpeed,game);
        this.game = game;

    }

    public void update() {

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

    public void updatePos() {

        int xSpeed = 0;
        int ySpeed = 0;
        isMoving=false;
        if (isLeft()) {
            xSpeed -= getMovementSpeed();
            setFacingDir(1);
            playerAction = RUNNING_LEFT;
            isMoving=true;
        }
        if (isRight()) {
            xSpeed += getMovementSpeed();
            setFacingDir(0);
            playerAction = RUNNING_RIGHT;
            isMoving=true;
        }
        if (isDown()) {
            ySpeed += getMovementSpeed();
            setFacingDir(3);
            playerAction = RUNNING_DOWN;
            isMoving=true;
        }
        if (isUp()) {
            ySpeed -= getMovementSpeed();
            setFacingDir(2);
            playerAction = RUNNING_UP;
            isMoving=true;
        }
        if (!isMoving) {
            playerAction = IDLE;

        }
        game.getCollisionChecker().handleCollision(this, game.getEntities(),xSpeed,ySpeed);
    }


    public abstract void render(Graphics g);
    @Override
    public String toString() {
        return "PLAYER";
    }
}

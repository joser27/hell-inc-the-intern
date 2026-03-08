package Model.entities;

import Controller.GameController;
import Model.Game;
import Model.utilz.LoadSave;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import static Model.utilz.Constants.PlayerConstants.*;

/** Player character for Demonic Contractor — movement and sprite only; no combat abilities. */
public class Player1 extends Player {

    private BufferedImage[][] img;

    public Player1(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
        setBufferedImage(LoadSave.GetSpriteAtlas(LoadSave.PLAYER2_ATLAS));
        img = new BufferedImage[24][8];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 8; j++) {
                Image scaledImage = getBufferedImage().getSubimage((768/24) * i, (256/8) * j, 768/24, 256/8).getScaledInstance(26 * GameController.SCALE, 26 * GameController.SCALE, Image.SCALE_DEFAULT);
                BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bufferedImage.getGraphics();
                g.drawImage(scaledImage, 0, 0, null);
                g.dispose();
                img[i][j] = bufferedImage;
            }
        }
    }
    public void respawn() {
        setXHitBox(13*GameController.TILE_SIZE);
        setYHitBox(8*GameController.TILE_SIZE);
        setHealth(100);
    }

    public void updatePos() {

        float xSpeed = 0;
        float ySpeed = 0;
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
        game.getCollisionChecker().handleCollision(this, game.getEntities(), game.getLevelLoader().getWorld(), GameController.TILE_SIZE, xSpeed, ySpeed);
    }
    public void update() {
        if (hasSlowEffect) {
            if (!hasSetSlowEffect) {
                setMovementSpeed(getBaseMovementSpeed()/4);
                hasSetSlowEffect=true;
            }
            slowEffectTick++;
            if (slowEffectTick>=slowEffectCD) {
                setMovementSpeed(getBaseMovementSpeed());
                hasSlowEffect=false;
                hasSetSlowEffect=false;
                slowEffectTick=0;
            }
        }
        updateAnimationTick();
        updatePos();
    }

    /** Current sprite for View to draw. */
    public BufferedImage getCurrentSprite() {
        return img[getAniIndex() + getAnimationCol()][getAnimationRow()];
    }
}

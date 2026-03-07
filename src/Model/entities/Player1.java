package Model.entities;

import Controller.GameController;
import Model.Game;
import Model.entities.abilites.*;
import Model.utilz.LoadSave;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static Model.utilz.Constants.PlayerConstants.*;

/*
 * The Tagger
 */
public class Player1 extends Player {

    private int speedBoostLimit = 0;
    private int speedBoostUsages = 10;
    private boolean speedBoostOn = false;
    float prevMS = getMovementSpeed();
    boolean godMode = false;
    private BufferedImage[][] img;
    //Normal attack
    private MeleeAttack meleeAttack;
    //Smash ability
    private Smash smash;
    private Shield shield;
    private Roar roar;

    public Player1(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
        setBufferedImage(LoadSave.GetSpriteAtlas(LoadSave.PLAYER2_ATLAS));
        //24col x 8row
        img = new BufferedImage[24][8];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 8; j++) {
                Image scaledImage = getBufferedImage().getSubimage((768/24) * i, (256/8) * j, 768/24, 256/8).getScaledInstance(26 * GameController.SCALE, 26 * GameController.SCALE, Image.SCALE_DEFAULT);

                // Convert Image to BufferedImage
                BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bufferedImage.getGraphics();
                g.drawImage(scaledImage, 0, 0, null);
                g.dispose();

                img[i][j] = bufferedImage;
            }
        }


        meleeAttack = new MeleeAttack(this,GameController.SCALE*12, getxPos(),getyPos(), 120);
        smash = new Smash(this,GameController.SCALE,getxPos(),getyPos(), 600);
        shield = new Shield(this,GameController.SCALE,getxPos(),getyPos(), 960);//4secs
        roar = new Roar(this,GameController.SCALE,getxPos(),getyPos(), 120);

    }
    public void respawn() {
        setXHitBox(13*GameController.TILE_SIZE);
        setYHitBox(8*GameController.TILE_SIZE);
        setHealth(100);
    }
    public void useShield() {
        shield.useShield();

    }
    public void speedBoost() {
        speedBoostOn = true;
    }
    public void attack() {
        meleeAttack.abilityUsed=true;//FOR UI

    }
    public void smashAttack(boolean isHolding) {
        smash.smashAttack(isHolding);

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
//        attackingUpdate();
        meleeAttack.update();
        smash.update();
        shield.update();
        roar.update();
        boostUpdate();

    }
    public void useRoar() {
        roar.useRoar();
    }

    public void boostUpdate() {
        if (speedBoostOn && speedBoostUsages>0) {
            godMode = true;
            speedBoostLimit++;
            setMovementSpeed(2.0f);

            if (speedBoostLimit >= 15) {
                godMode = false;
                speedBoostUsages--;
                speedBoostOn = false;
                speedBoostLimit = 0;
                setMovementSpeed(prevMS);
            }
        }
    }

    /** Current sprite for View to draw. Model does not draw. */
    public BufferedImage getCurrentSprite() {
        return img[getAniIndex() + getAnimationCol()][getAnimationRow()];
    }
    public MeleeAttack getMeleeAttack() { return meleeAttack; }
    public Shield getShield() { return shield; }
    public Roar getRoar() { return roar; }
    public Smash getSmash() {
        return smash;
    }

    public boolean isGodMode() {
        return godMode;
    }

    public Rectangle2D.Float getAttackHitBox() {
        return meleeAttack.attackHitBox;
    }

    public void setAttackHitBox(Rectangle2D.Float attackHitBox) {
        meleeAttack.attackHitBox = attackHitBox;
    }

    public boolean isAttackingMelee() {
        return meleeAttack.abilityUsed;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    public int getSpeedBoostUsages() {
        return speedBoostUsages;
    }



}

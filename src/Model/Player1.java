package Model;

import Controller.GameController;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static Model.utilz.Constants.PlayerConstants.*;

/*
 * The Tagger
 */
public class Player1 extends Player{

    private int speedBoostLimit = 0;
    private int speedBoostUsages = 10;
    private boolean speedBoostOn = false;
    float prevMS = getMovementSpeed();
    boolean godMode = false;
    private BufferedImage[][] img;
    private Rectangle2D.Float attackHitBox;
    private boolean attackingMelee  = false;
    private int attackingCD = 20;
    private int attackingTimer;
    private int attackingDuration;
    private boolean canAttack = true;

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

        attackHitBox = new Rectangle2D.Float(getxPos(),getyPos(),30,30);
    }
    public void respawn() {
        setXHitBox(13*GameController.TILE_SIZE);
        setYHitBox(8*GameController.TILE_SIZE);
        setHealth(100);
    }
    public void speedBoost() {
        speedBoostOn = true;
    }
    public void attack() {
        if (canAttack) {
            canAttack = false;
            attackingMelee = true;
        }

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
        game.getCollisionChecker().handleCollision(this, game.getEntities(),xSpeed,ySpeed);
    }
    public void update() {
        updateAnimationTick();
        updatePos();
        attackingTimer++;
        if (attackingTimer > attackingCD) {
            attackingTimer=0;
            canAttack = true;
        }
        if (attackingMelee) {
            attackingDuration++;
            if (attackingDuration>150) {
                attackingDuration=0;
                attackingTimer=0;
                attackingMelee=false;
            }
        }

         switch(getFacingDir()) {//0 = right, 1 = left, 2 = up, 3 = down
             case 0 -> {
                 attackHitBox.x = getxPos()+10;
                 attackHitBox.y = getyPos()-6;
                 if (attackingMelee)playerAction = OGRE_ATTACK_RIGHT;

             }
             case 1 -> {
                 attackHitBox.x = getxPos()-18;
                 attackHitBox.y = getyPos()-6;
                 if (attackingMelee)playerAction = OGRE_ATTACK_LEFT;
             }
             case 2 -> {
                 attackHitBox.x = getxPos()-6;
                 attackHitBox.y = getyPos()-15;
                 if (attackingMelee)playerAction = OGRE_ATTACK_UP;
             }
             case 3 -> {
                 attackHitBox.x = getxPos()-6;
                 attackHitBox.y = getyPos()+4;
                 if (attackingMelee)playerAction = OGRE_ATTACK_DOWN;
             }
         }


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
//    @Override
//    public void updatePos() {
//        int xSpeed = 0;
//        int ySpeed = 0;
//        if (isLeft()) {
//            xSpeed -= getMovementSpeed();
//        }
//        if (isRight()) {
//            xSpeed += getMovementSpeed();
//        }
//        if (isDown()) {
//            ySpeed += getMovementSpeed();
//
//        }
//        if (isUp()) {
//            ySpeed -= getMovementSpeed();
//        }
//        game.getCollisionChecker().handleCollision(this, game.getEntities(),xSpeed,ySpeed);
//    }



    public void render(Graphics g,int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.WHITE);
        g.drawRect((int) attackHitBox.x - xLvlOffset, (int) attackHitBox.y-yLvlOffset, 30, 30);
        g.drawImage(img[aniIndex + animationCol][animationRow], (getxPos() - 9 * GameController.SCALE) - xLvlOffset, getyPos() - 8 * GameController.SCALE- yLvlOffset, null);
        //Hitbox
        // g.setColor(Color.YELLOW);
        // g.drawRect(getxPos() - xLvlOffset, getyPos(), (int) getHitBox().width, (int) getHitBox().height);

    }

    public boolean isGodMode() {
        return godMode;
    }

    public Rectangle2D.Float getAttackHitBox() {
        return attackHitBox;
    }

    public void setAttackHitBox(Rectangle2D.Float attackHitBox) {
        this.attackHitBox = attackHitBox;
    }

    public boolean isAttackingMelee() {
        return attackingMelee;
    }

    public void setGodMode(boolean godMode) {
        this.godMode = godMode;
    }

    public int getSpeedBoostUsages() {
        return speedBoostUsages;
    }
}

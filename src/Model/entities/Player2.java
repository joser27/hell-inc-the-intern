package Model.entities;

import Controller.GameController;
import Model.*;
import Model.entities.abilites.*;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import static Model.utilz.Constants.PlayerConstants.*;

/*
 * The Runner
 */
public class Player2 extends Player {


    private RangedAttack rangedAttacks;
    Volley volleyShot;
    Hawkshot hawkshot;
    private EnchantedArrow enchantedArrow;
    private boolean usedRangedAttack = false;
    private int AutoAttackTick;
    public boolean canShootVolley = false;
    public int volleyDelayShootTick;
    private boolean canShootEnchantedArrow = false;
    private int enchantedArrowDelayShootTick;
    private BufferedImage[][] img;
    private int baseAttackSpeed = 50;
    private int attackSpeed = baseAttackSpeed;
    RangerFocus rangerFocus;
    Image arrowImage;
//    private String playerAction = RUNNING_DOWN;
//    private int aniTick, aniIndex, aniSpeed = 15;
//    private int actionOffset;
//    private int animationCol, animationRow, animationFrames;
//    private String lastPlayerAction = "";
//    private boolean isMoving = false;


    public Player2(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
        //Images
        setBufferedImage(LoadSave.GetSpriteAtlas(LoadSave.PLAYER1_ATLAS));
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
        //Arrow
        arrowImage = LoadSave.GetSpriteAtlas(LoadSave.ARROW_PROJECTILE).getScaledInstance(20,20, Image.SCALE_DEFAULT);

        //abilities
        rangedAttacks = new RangedAttack(this, arrowImage, GameController.SCALE,(int)this.getHitBox().x,(int)this.getHitBox().y,120);
        rangerFocus = new RangerFocus(this,GameController.SCALE,(int)this.getHitBox().x,(int)this.getHitBox().y,700);
        enchantedArrow = new EnchantedArrow(this,GameController.SCALE,(int)this.getHitBox().x,(int)this.getHitBox().y,3600);
        volleyShot = new Volley(this,GameController.SCALE,(int)this.getHitBox().x,(int)this.getHitBox().y,120);
        hawkshot = new Hawkshot(this,GameController.SCALE,(int)this.getHitBox().x,(int)this.getHitBox().y,1200);//5sec


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
//    public void updateAutoAttack() {
//        if (canAutoAttack) {
//            setShootDir();
//            AutoAttackTick++;
//            if (AutoAttackTick > attackSpeed) {
//                canAutoAttack = false;
//                AutoAttackTick = 0;
//
//                // 0 = right, 1 = left, 2 = up, 3 = down
//                RangedAttack frostShot = new RangedAttack(this,GameController.SCALE,getxPos() + getWidth() / 2, getyPos() + getHeight() / 2,120);
//                switch (getFacingDir()) {
//                    case 0:
//                        frostShot.setHorizontal(true);
//                        frostShot.setBulletSpeed(frostShot.getBulletSpeed());
//                        break;
//                    case 1:
//                        frostShot.setHorizontal(true);
//                        frostShot.setBulletSpeed(frostShot.getBulletSpeed() * -1);
//                        break;
//                    case 2:
//                        frostShot.setVertical(true);
//                        frostShot.setBulletSpeed(frostShot.getBulletSpeed() * -1);
//                        break;
//                    case 3:
//                        frostShot.setVertical(true);
//                        frostShot.setBulletSpeed(frostShot.getBulletSpeed());
//                        break;
//                }
//                rangedAttacks.add(frostShot);
//            }
//        }
//        updateAndRemoveAutoAttacks();
//    }
//
//    public void updateAndRemoveAutoAttacks() {
//        if (rangedAttacks != null) {
//            Iterator<RangedAttack> iterator = rangedAttacks.iterator();
//
//            while (iterator.hasNext()) {
//                RangedAttack frostShot = iterator.next();
//                frostShot.update();
//
//                if (frostShot.isBulletDecayed()) {
//                    iterator.remove();
//                }
//            }
//        }
//    }
//    private void setShootDir() {
//        switch (getFacingDir()) {
//            case 0:
//                playerAction = HUMAN_ATTACK_RIGHT;
//                break;
//            case 1:
//                playerAction = HUMAN_ATTACK_LEFT;
//                break;
//            case 2:
//                playerAction = HUMAN_ATTACK_UP;
//                break;
//            case 3:
//                playerAction = HUMAN_ATTACK_DOWN;
//                break;
//        }
//    }
//    public void updateVolley() {
//        if (canShootVolley) {
//            setShootDir();
//            volleyDelayShootTick++;
//            if (volleyDelayShootTick > 150) {
//                canShootVolley = false;
//                volleyDelayShootTick = 0;
//                volleyShot = new Volley(this,GameController.SCALE,getxPos() + getWidth() / 2, getyPos() + getHeight() / 2,240);
//                switch (getFacingDir()) {
//                    case 0:
//                        volleyShot.setHorizontal(true);
//                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed());
//                        break;
//                    case 1:
//                        volleyShot.setHorizontal(true);
//                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed() * -1);
//                        break;
//                    case 2:
//                        volleyShot.setVertical(true);
//                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed() * -1);
//                        break;
//                    case 3:
//                        volleyShot.setVertical(true);
//                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed());
//                        break;
//                }
//            }
//        }
//    }
//    public void updateEnchantedArrow() {
//        if (canShootEnchantedArrow) {
//            setShootDir();
//            enchantedArrowDelayShootTick++;
//            if (enchantedArrowDelayShootTick > 150) {
//                canShootEnchantedArrow = false;
//                enchantedArrowDelayShootTick = 0;
//                enchantedArrow = new EnchantedArrow(this,GameController.SCALE,getxPos() + getWidth() / 2, getyPos() + getHeight() / 2, 1000);
//                switch (getFacingDir()) {
//                    case 0:
//                        enchantedArrow.setHorizontal(true);
//                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed());
//                        break;
//                    case 1:
//                        enchantedArrow.setHorizontal(true);
//                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed() * -1);
//                        break;
//                    case 2:
//                        enchantedArrow.setVertical(true);
//                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed() * -1);
//                        break;
//                    case 3:
//                        enchantedArrow.setVertical(true);
//                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed());
//                        break;
//                }
//            }
//        }
//        if (enchantedArrow!=null) {
//            enchantedArrow.update();
//            if (enchantedArrow.isBulletDecayed()) {
//                enchantedArrow = null;
//            }
//        }
//    }
    public void update() {
        updateAnimationTick();
        updatePos();
        //updateAutoAttack();

        rangedAttacks.update();
        rangerFocus.update();
        enchantedArrow.update();
        volleyShot.update();
        hawkshot.update();

//        volleyShot.update();
        //updateVolley();
        //updateEnchantedArrow();
    }

    public void autoAttack() {
        if (!rangedAttacks.abilityUsed) {
            rangedAttacks.shootBullet();
        }
    }

    public void rangerFocus() {
//        rangerFocus.rangerFocus = true;
        rangerFocus.abilityUsed = true;

    }
    public void shootVolley() {
        volleyShot.shootVolley();
    }
    public void shootHawkshot() {
        hawkshot.abilityUsed=true;

    }
    public void shootEnchantedArrow() {
        enchantedArrow.abilityUsed = true;
    }

//    public void renderFrostShot(Graphics g, int xLvlOffset, int yLvlOffset) {
//        if (rangedAttacks.size()  >0) {
//            Iterator<RangedAttack> iterator = rangedAttacks.iterator();
//            while (iterator.hasNext()) {
//                RangedAttack frostShot = iterator.next();
//                frostShot.render(g,xLvlOffset,yLvlOffset);
//            }
//        }
//    }

//    public void renderVolley(Graphics g, int xLvlOffset, int yLvlOffset) {
//        if (rangedAttacks.size()  >0) {
//            Iterator<RangedAttack> iterator = rangedAttacks.iterator();
//            while (iterator.hasNext()) {
//                RangedAttack frostShot = iterator.next();
//                frostShot.render(g,xLvlOffset,yLvlOffset);
//            }
//        }
//    }

    public void renderUI(Graphics g) {
        rangerFocus.renderUI(g);
        enchantedArrow.renderUI(g);
        volleyShot.renderUI(g);
        hawkshot.renderUI(g);
    }
    public void render(Graphics g,int xLvlOffset, int yLvlOffset) {
        g.setColor(new Color(178, 26, 26));
        g.drawRect((int) (getHitBox().x -10)-xLvlOffset, (int) (getHitBox().y - 20)-yLvlOffset,50,15);
        g.fillRect((int) (getHitBox().x -10)-xLvlOffset, (int) (getHitBox().y - 20)-yLvlOffset, (int) (getHealth()/2),15);
        g.setColor(new Color(255, 0, 0));
        g.fillRect((int) (getHitBox().x -10)-xLvlOffset, (int) (getHitBox().y - 20)-yLvlOffset, (int) (getHealth()/2),10);
        volleyShot.render(g,xLvlOffset,yLvlOffset);
        rangedAttacks.render(g,xLvlOffset,yLvlOffset);
//        if (usedRangedAttack) {
//            System.out.println("YEEEEEEEEET!");
//            for (RangedAttack rangedAttack : rangedAttacks) {
//                rangedAttack.render(g,xLvlOffset,yLvlOffset);
//            }
//        }
//        if (volleyShot!=null) {
//            volleyShot.render(g, xLvlOffset, yLvlOffset);
//        }
//        if (enchantedArrow!=null) {
//            enchantedArrow.render(g,xLvlOffset,yLvlOffset);
//        }
        //[aniIndex ADD COL]     [ADD ROW] (Of Sprite)
        g.drawImage(img[aniIndex + animationCol][animationRow],(getxPos()-9*GameController.SCALE) - xLvlOffset, getyPos()-8*GameController.SCALE- yLvlOffset,null);
        g.setColor(Color.BLACK);

        //g.drawString(Integer.toString(rangerFocus.rangerFocusCoolDownTick),160,800);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
        //Hit box
//        g.setColor(Color.RED);
//        g.drawRect(getxPos()-xLvlOffset,getyPos()- yLvlOffset, (int) getHitBox().width, (int) getHitBox().height);
    }

//    public ArrayList<RangedAttack> getBullets() {
//        return rangedAttacks;
//    }

    public Volley getVolleyShot() {
        return volleyShot;
    }
    public void removeVolleyShot() {
        volleyShot = null;
    }
    public RangedAttack getRangedAttacks() {
        return rangedAttacks;
    }

}

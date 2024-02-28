package Model;

import Controller.GameController;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import static Model.utilz.Constants.PlayerConstants.*;

/*
 * The Runner
 */
public class Player2 extends Player{

    private int landMineCount = 10;
    private ArrayList<LandMine> landMine;
    private ArrayList<FrostShot> frostShots;
    private Volley volleyShot;
    private EnchantedArrow enchantedArrow;
    private boolean canShootFrostShot = false;
    private int frostShotDelayShootTick;
    private boolean canShootVolley = false;
    private int volleyDelayShootTick;
    private boolean canShootEnchantedArrow = false;
    private int enchantedArrowDelayShootTick;
    private BufferedImage[][] img;

//    private String playerAction = RUNNING_DOWN;
//    private int aniTick, aniIndex, aniSpeed = 15;
//    private int actionOffset;
//    private int animationCol, animationRow, animationFrames;
//    private String lastPlayerAction = "";
//    private boolean isMoving = false;


    public Player2(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
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
        landMine = new ArrayList<>();
        frostShots = new ArrayList<>();

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
    public void updateFrostShot() {

        if (canShootFrostShot) {
            setShootDir();

            frostShotDelayShootTick++;
            if (frostShotDelayShootTick > 50) {
                canShootFrostShot = false;
                frostShotDelayShootTick = 0;

                // 0 = right, 1 = left, 2 = up, 3 = down
                FrostShot frostShot = new FrostShot(this,GameController.SCALE,getxPos() + getWidth() / 2, getyPos() + getHeight() / 2);
                switch (getFacingDir()) {
                    case 0:
                        frostShot.setHorizontal(true);
                        frostShot.setBulletSpeed(frostShot.getBulletSpeed());
                        break;
                    case 1:
                        frostShot.setHorizontal(true);
                        frostShot.setBulletSpeed(frostShot.getBulletSpeed() * -1);
                        break;
                    case 2:
                        frostShot.setVertical(true);
                        frostShot.setBulletSpeed(frostShot.getBulletSpeed() * -1);
                        break;
                    case 3:
                        frostShot.setVertical(true);
                        frostShot.setBulletSpeed(frostShot.getBulletSpeed());
                        break;
                }
                frostShots.add(frostShot);
            }
        }
    }
    public void updateLandMine() {
        if (landMine.size()>0) {

            Iterator<LandMine> iterator = landMine.iterator();

            while (iterator.hasNext()) {
                LandMine landMine = iterator.next();
                landMine.update();

                if (landMine.getLandMineTimer()>landMine.getLandMineDecayTime()) {
                    landMine.explode();
                    if (landMine.getLandMineTimer()>landMine.getLandMineDetonateTime())
                        iterator.remove();
                }
            }
        }
    }
    public void updateAndRemoveFrostShot() {
        if (frostShots != null) {
            Iterator<FrostShot> iterator = frostShots.iterator();

            while (iterator.hasNext()) {
                FrostShot frostShot = iterator.next();
                frostShot.update();

                if (frostShot.isBulletDecayed()) {
                    iterator.remove();
                }
            }
        }
    }
    private void setShootDir() {
        switch (getFacingDir()) {
            case 0:
                playerAction = HUMAN_ATTACK_RIGHT;
                break;
            case 1:
                playerAction = HUMAN_ATTACK_LEFT;
                break;
            case 2:
                playerAction = HUMAN_ATTACK_UP;
                break;
            case 3:
                playerAction = HUMAN_ATTACK_DOWN;
                break;
        }
    }
    public void updateVolley() {
        if (canShootVolley) {
            setShootDir();
            volleyDelayShootTick++;
            if (volleyDelayShootTick > 150) {
                canShootVolley = false;
                volleyDelayShootTick = 0;
                volleyShot = new Volley(getxPos() + getWidth() / 2, getyPos() + getHeight() / 2);
                switch (getFacingDir()) {
                    case 0:
                        volleyShot.setHorizontal(true);
                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed());
                        break;
                    case 1:
                        volleyShot.setHorizontal(true);
                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed() * -1);
                        break;
                    case 2:
                        volleyShot.setVertical(true);
                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed() * -1);
                        break;
                    case 3:
                        volleyShot.setVertical(true);
                        volleyShot.setBulletSpeed(volleyShot.getBulletSpeed());
                        break;
                }
            }
        }
    }
    public void updateEnchantedArrow() {
        if (canShootEnchantedArrow) {
            setShootDir();
            enchantedArrowDelayShootTick++;
            if (enchantedArrowDelayShootTick > 150) {
                canShootEnchantedArrow = false;
                enchantedArrowDelayShootTick = 0;
                enchantedArrow = new EnchantedArrow(this,GameController.SCALE,getxPos() + getWidth() / 2, getyPos() + getHeight() / 2);
                switch (getFacingDir()) {
                    case 0:
                        enchantedArrow.setHorizontal(true);
                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed());
                        break;
                    case 1:
                        enchantedArrow.setHorizontal(true);
                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed() * -1);
                        break;
                    case 2:
                        enchantedArrow.setVertical(true);
                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed() * -1);
                        break;
                    case 3:
                        enchantedArrow.setVertical(true);
                        enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed());
                        break;
                }
            }
        }
        if (enchantedArrow!=null) {
            enchantedArrow.update();
            if (enchantedArrow.isBulletDecayed()) {
                enchantedArrow = null;
            }
        }
    }
    public void update() {

        updateAnimationTick();
        updatePos();
        updateFrostShot();
        updateAndRemoveFrostShot();
        updateLandMine();
        updateVolley();
        updateEnchantedArrow();
    }


    public void placeMine() {
        if (landMineCount > 0) {
            landMineCount--;
            landMine.add(new LandMine(getxPos()+5, getyPos()+15, 10, 10));
        }
    }
    public void shootFrostShot() {
        canShootFrostShot = true;
    }
    public void shootVolley() {
        canShootVolley = true;
    }
    public void shootEnchantedArrow() {
        canShootEnchantedArrow = true;
    }
    public void renderMines(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (landMine !=null) {
            for (LandMine mine : landMine) {
                mine.render(g,xLvlOffset,yLvlOffset);
            }
        }
    }
    public void renderFrostShot(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (frostShots.size()  >0) {
            Iterator<FrostShot> iterator = frostShots.iterator();
            while (iterator.hasNext()) {
                FrostShot frostShot = iterator.next();
                frostShot.render(g,xLvlOffset,yLvlOffset);
            }
        }
    }

    public void renderVolley(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (frostShots.size()  >0) {
            Iterator<FrostShot> iterator = frostShots.iterator();
            while (iterator.hasNext()) {
                FrostShot frostShot = iterator.next();
                frostShot.render(g,xLvlOffset,yLvlOffset);
            }
        }
    }


    public void render(Graphics g,int xLvlOffset, int yLvlOffset) {


        if (volleyShot!=null) {
            volleyShot.render(g, xLvlOffset, yLvlOffset);
        }
        if (enchantedArrow!=null) {
            enchantedArrow.render(g,xLvlOffset,yLvlOffset);
        }



        //[aniIndex ADD COL]     [ADD ROW] (Of Sprite)
        g.drawImage(img[aniIndex + animationCol][animationRow],(getxPos()-9*GameController.SCALE) - xLvlOffset, getyPos()-8*GameController.SCALE- yLvlOffset,null);






//        g.setColor(Color.BLACK);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
        //Hit box
//        g.setColor(Color.RED);
//        g.drawRect(getxPos()-xLvlOffset,getyPos()- yLvlOffset, (int) getHitBox().width, (int) getHitBox().height);
    }

    public ArrayList<LandMine> getLandMine() {
        return landMine;
    }

    public ArrayList<FrostShot> getBullets() {
        return frostShots;
    }

    public int getLandMineCount() {
        return landMineCount;
    }

    public Volley getVolleyShot() {
        return volleyShot;
    }
    public void removeVolleyShot() {
        volleyShot = null;
    }
}

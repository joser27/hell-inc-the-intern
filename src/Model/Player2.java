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
    private ArrayList<Bullet> bullets;
    private boolean canShoot = false;
    private int shootCD = 60;
    private int shootTimer = 0;
    private VectorGun vectorGun;
    private Shotgun shotgun;
    private BufferedImage[][] img;
    private int bowDelayShootTick;
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
        bullets = new ArrayList<>();



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

        if (canShoot) {
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

            bowDelayShootTick++;
            if (bowDelayShootTick > 50) {
                canShoot = false;
                bowDelayShootTick = 0;

                // 0 = right, 1 = left, 2 = up, 3 = down
                Bullet bullet = new Bullet(getxPos() + getWidth() / 2, getyPos() + getHeight() / 2);
                switch (getFacingDir()) {
                    case 0:
                        bullet.setHorizontal(true);
                        bullet.setBulletSpeed(bullet.getBulletSpeed());
                        break;
                    case 1:
                        bullet.setHorizontal(true);
                        bullet.setBulletSpeed(bullet.getBulletSpeed() * -1);
                        break;
                    case 2:
                        bullet.setVertical(true);
                        bullet.setBulletSpeed(bullet.getBulletSpeed() * -1);
                        break;
                    case 3:
                        bullet.setVertical(true);
                        bullet.setBulletSpeed(bullet.getBulletSpeed());
                        break;
                }
                bullets.add(bullet);
            }
        }



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
        if (bullets!= null) {
            Iterator<Bullet> iterator = bullets.iterator();

            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                bullet.update();

                if (bullet.isBulletDecayed()) {
                    iterator.remove();
                }
            }

        }

    }
//    private void updateAnimationTick() {
//        aniTick++;
//        if (aniTick >= aniSpeed) {
//            aniTick=0;
//            aniIndex++;
//            int[] action = GetSpriteAmountColRow(playerAction);
//            if (playerAction == 0) {// Running down
//                actionOffset = action[1];
//                System.out.println(action[1]);
//            }
//            if (aniIndex >= actionOffset+action[2]) {
//                aniIndex = actionOffset;
//            }
//        }
//    }


//    @Override
//    public void updatePos() {
//
//        int xSpeed = 0;
//        int ySpeed = 0;
//        isMoving=false;
//        if (isLeft()) {
//            xSpeed -= getMovementSpeed();
//            setFacingDir(1);
//            playerAction = RUNNING_LEFT;
//            isMoving=true;
//        }
//        if (isRight()) {
//            xSpeed += getMovementSpeed();
//            setFacingDir(0);
//            playerAction = RUNNING_RIGHT;
//            isMoving=true;
//        }
//        if (isDown()) {
//            ySpeed += getMovementSpeed();
//            setFacingDir(3);
//            playerAction = RUNNING_DOWN;
//            isMoving=true;
//        }
//        if (isUp()) {
//            ySpeed -= getMovementSpeed();
//            setFacingDir(2);
//            playerAction = RUNNING_UP;
//            isMoving=true;
//        }
//        if (!isMoving) {
//            playerAction = IDLE;
//
//        }
//        game.getCollisionChecker().handleCollision(this, game.getEntities(),xSpeed,ySpeed);
//    }
    public void placeMine() {
        if (landMineCount > 0) {
            landMineCount--;
            landMine.add(new LandMine(getxPos()+5, getyPos()+15, 10, 10));
        }
    }
    public void shoot() {
        canShoot = true;
    }

    public void render(Graphics g,int xLvlOffset) {

        if (landMine !=null) {
            for (LandMine mine : landMine) {
                mine.render(g);
            }
        }

                        //[aniIndex ADD COL]     [ADD ROW] (Of Sprite)

        g.drawImage(img[aniIndex + animationCol][animationRow],(getxPos()-9*GameController.SCALE) - xLvlOffset, getyPos()-8*GameController.SCALE,null);


        if (bullets.size()  >0) {
            Iterator<Bullet> iterator = bullets.iterator();
            while (iterator.hasNext()) {
                Bullet bullet = iterator.next();
                bullet.render(g);
                }
        }



//        g.setColor(Color.BLACK);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
        //Hit box
        g.setColor(Color.RED);
        g.drawRect(getxPos()-xLvlOffset,getyPos(), (int) getHitBox().width, (int) getHitBox().height);
    }

    public ArrayList<LandMine> getLandMine() {
        return landMine;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public int getLandMineCount() {
        return landMineCount;
    }
}

package Model;

import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
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
    private boolean canShoot = true;
    private int shootCD = 60;
    private int shootTimer = 0;
    private int landMineTimer = 0;
    private int landMineDecayTime = 800;
    private int landMineDetonateTime = 900;
    private BufferedImage[][] img;
    private String playerAction = RUNNING_DOWN;
    private int aniTick, aniIndex, aniSpeed = 15;
    private int actionOffset;
    private int animationCol, animationRow, animationFrames;
    private String lastPlayerAction = "";
    private boolean isMoving = false;
    int[] action;

    public Player2(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
        setBufferedImage(LoadSave.GetSpriteAtlas(LoadSave.PLAYER1_ATLAS));
        img = new BufferedImage[24][8];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 8; j++) {
                //768 x 256
//                img[i][j] = getBufferedImage().getSubimage(i,j,(768/24) * i,(256/8) * j);
                img[i][j] = getBufferedImage().getSubimage((768/24) * i, (256/8) * j, 768/24, 256/8);

            }
        }
        landMine = new ArrayList<>();
        bullets = new ArrayList<>();
    }


    public void update() {
        updateAnimationTick();
        shootTimer++;
        if (shootTimer>= shootCD) {
            canShoot = true;
        } else {
            canShoot = false;
        }
        updatePos();


        if (landMine.size()>0) {
            landMineTimer++;
            if (landMineTimer >= landMineDecayTime) {
                landMine.get(0).explode();

                if (landMineTimer >= landMineDetonateTime) {
                    landMine.remove(0);
                    landMineTimer = 0;
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
    private void updateAnimationTick() {
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

    @Override
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
    public void placeMine() {
        if (landMineCount > 0) {
            landMineCount--;
            landMine.add(new LandMine(getxPos(), getyPos(), 10, 10));
        }
    }
    public void shoot() {
        if (canShoot) {
            shootTimer = 0;
            //0 = right, 1 = left, 2 = up, 3 = down
            Bullet bullet = new Bullet(getxPos() + getWidth() / 2, getyPos() + getHeight() / 2);
            if (getFacingDir() == 0) {
                bullet.setHorizontal(true);
                bullet.setBulletSpeed(bullet.getBulletSpeed());
            } else if (getFacingDir() == 1) {
                bullet.setHorizontal(true);
                bullet.setBulletSpeed(bullet.getBulletSpeed() * -1);
            } else if (getFacingDir() == 2) {
                bullet.setVertical(true);
                bullet.setBulletSpeed(bullet.getBulletSpeed() * -1);
            } else if (getFacingDir() == 3) {
                bullet.setVertical(true);
                bullet.setBulletSpeed(bullet.getBulletSpeed());
            }

            bullets.add(bullet);
        }
    }
    @Override
    public void render(Graphics g) {
        //Hit box
//        g.setColor(Color.RED);
//        g.drawRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);


                        //[aniIndex ADD COL]     [ADD ROW] (Of Sprite)
        g.drawImage(img[aniIndex + animationCol][animationRow].getScaledInstance(80,80,Image.SCALE_DEFAULT),getxPos()-25, getyPos()-29,null);

        BufferedImage gun = LoadSave.GetSpriteAtlas(LoadSave.PISTOL_STATIC_IMG);

        if (!canShoot) {
            switch (getFacingDir()) {
                case 0:
                    // Facing right (no inversion)
                    g.drawImage(gun, getxPos() + 15, getyPos(), null);

                    // Char Sprite face
                    //g.drawImage(img[2][2].getScaledInstance(80,80,Image.SCALE_DEFAULT),getxPos()-25, getyPos()-25,null);

                    break;
                case 1:
                    // Facing left (horizontal flip)
                    AffineTransform txLeft = AffineTransform.getScaleInstance(-1, 1);
                    txLeft.translate(-gun.getWidth(null), 0);
                    AffineTransformOp flipOpLeft = new AffineTransformOp(txLeft, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage flippedGunLeft = flipOpLeft.filter(gun, null);

                    g.drawImage(flippedGunLeft, getxPos() - 15, getyPos(), null);

                    // Char Sprite face
                    //g.drawImage(img[2][6].getScaledInstance(80,80,Image.SCALE_DEFAULT),getxPos()-25, getyPos()-25,null);

                    break;
                case 2:
                    // Facing up (vertical flip, horizontal flip, and rotate 90 degrees clockwise)
                    AffineTransform txUp = AffineTransform.getScaleInstance(-1, -1);
                    txUp.translate(-gun.getWidth(null), -gun.getHeight(null));
                    txUp.rotate(Math.PI / 2); // Rotate 90 degrees clockwise
                    AffineTransformOp flipOpUp = new AffineTransformOp(txUp, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage flippedGunUp = flipOpUp.filter(gun, null);

                    g.drawImage(flippedGunUp, getxPos() - 30, getyPos() - 10, null);

                    // Char Sprite face
                    //g.drawImage(img[2][4].getScaledInstance(80,80,Image.SCALE_DEFAULT),getxPos()-25, getyPos()-25,null);

                    break;
                case 3:
                    // Facing down (inversion)
                    AffineTransform txDown = AffineTransform.getScaleInstance(1, 1);
                    txDown.translate(gun.getWidth(null), gun.getHeight(null));
                    txDown.rotate(Math.PI / 2); // Rotate 90 degrees clockwise
                    AffineTransformOp flipOpDown = new AffineTransformOp(txDown, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage flippedGunDown = flipOpDown.filter(gun, null);

                    g.drawImage(flippedGunDown, getxPos(), getyPos() - 10, null);

                    // Char Sprite face
                    //g.drawImage(img[7][7].getScaledInstance(80,80,Image.SCALE_DEFAULT),getxPos()-25, getyPos()-25,null);

                    break;
                default:
                    break;

            }
        }

        Font font = new Font("Arial", Font.BOLD, 18);
        g.setFont(font);
        g.drawString("Player2 coords: " + getyPos()/48 + " " + getxPos()/48 + ", Mines: " + landMineCount + "; HP: " + getHealth(), 50, 150);

        if (landMine !=null) {
            for (LandMine mine : landMine) {
                mine.render(g);
            }
        }
        if (bullets !=null) {
            for (Bullet bullet : bullets) {
                bullet.render(g);
            }
        }

//        g.setColor(Color.BLACK);
//        //System.err.println(playerX + "|" + playerY);
//
//        g.fillRect((playerY*48),(playerX*48),48,48);
    }

    public ArrayList<LandMine> getLandMine() {
        return landMine;
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}

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


    public Player2(int xPos, int yPos, int width, int height, float movementSpeed, Game game) {
        super(xPos, yPos, width, height, movementSpeed, game);
        setBufferedImage(LoadSave.GetSpriteAtlas(LoadSave.PLAYER2_IMG));
        landMine = new ArrayList<>();
        bullets = new ArrayList<>();
    }


    public void update() {
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
    @Override
    public void updatePos() {
        if (isDown()) {
            setFacingDir(3);
        } else if (isUp()) {
            setFacingDir(2);
        } else if (isLeft()) {
            setFacingDir(1);
        } else if (isRight()) {
            setFacingDir(0);
        }
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

        g.setColor(Color.RED);
        g.fillRect(getxPos(),getyPos(), (int) getHitBox().width, (int) getHitBox().height);
        Image scaledImg = getBufferedImage().getScaledInstance(getWidth(),getHeight(),Image.SCALE_FAST);
        g.drawImage(scaledImg,getxPos(),getyPos(),null);

        if (!canShoot) {
            BufferedImage gun = LoadSave.GetSpriteAtlas(LoadSave.PISTOL_STATIC_IMG);

            switch (getFacingDir()) {
                case 0:
                    // Facing right (no inversion)
                    g.drawImage(gun, getxPos()+15, getyPos(), null);
                    break;
                case 1:
                    // Facing left (horizontal flip)
                    AffineTransform txLeft = AffineTransform.getScaleInstance(-1, 1);
                    txLeft.translate(-gun.getWidth(null), 0);
                    AffineTransformOp flipOpLeft = new AffineTransformOp(txLeft, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage flippedGunLeft = flipOpLeft.filter(gun, null);

                    g.drawImage(flippedGunLeft, getxPos()-15, getyPos(), null);
                    break;
                case 2:
                    // Facing up (vertical flip, horizontal flip, and rotate 90 degrees clockwise)
                    AffineTransform txUp = AffineTransform.getScaleInstance(-1, -1);
                    txUp.translate(-gun.getWidth(null), -gun.getHeight(null));
                    txUp.rotate(Math.PI / 2); // Rotate 90 degrees clockwise
                    AffineTransformOp flipOpUp = new AffineTransformOp(txUp, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    BufferedImage flippedGunUp = flipOpUp.filter(gun, null);

                    g.drawImage(flippedGunUp, getxPos()-30, getyPos()-10, null);
                    break;
                case 3:
                    // Facing down (no inversion)
                    g.drawImage(gun, getxPos(), getyPos(), null);
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

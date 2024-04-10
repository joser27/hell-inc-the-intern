package Model.entities.abilites;

import Model.entities.Player;
import Model.utilz.LoadSave;

import java.awt.*;

public class RangedAttack extends Ability {
    private Projectile bullet;
    private boolean vertical;
    private boolean horizontal;
    private int bulletSpeed = 6;
    private int bulletSize = 8;
    private int bulletDistance = 75;
    private int bulletUpTime = 0;
    private boolean bulletDecayed = false;
    Image img;
    public RangedAttack(Player player, Image img, int scale, int xPos, int yPos, int cd) {
        super(player,scale,xPos,yPos, cd);

//        bullet = new Projectile(player);
//        img = LoadSave.GetSpriteAtlas(LoadSave.ARROW_PROJECTILE).getScaledInstance(20,20,Image.SCALE_DEFAULT);
        this.img = img;
    }

    public void shootBullet() {
        abilityUsed=true;
        bullet = new Projectile(player,(int)player.getHitBox().x,(int)player.getHitBox().y,img,80);
        bullet.setProjectileDirection(player.getFacingDir());
    }
    @Override
    public void update() {
        updateUI();
        if (abilityUsed && bullet!=null) {
            bullet.updateProjectile();
            if (bullet.projectileIsDecayed) {
                bullet = null;
            }
        }
//        if (horizontal) {
//            bullet.hitBox.x += bulletSpeed;
//        } else if (vertical) {
//            bullet.hitBox.y += bulletSpeed;
//        }
//
//        bulletUpTime++;
//        if (bulletUpTime >= bulletDistance) {
//            bulletDecayed = true;
//        }
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
//                RangedAttack frostShot = new RangedAttack(this, GameController.SCALE,getxPos() + getWidth() / 2, getyPos() + getHeight() / 2,120);
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



    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        Graphics2D g2d = (Graphics2D) g.create();

        if (abilityUsed && bullet!=null) {
            bullet.render(g, xLvlOffset, yLvlOffset);

        }

//        // Set the rotation angle and adjustment based on the bullet's direction
//        double rotationAngle = 0.0;
//        int adjustmentX = 0;
//        int adjustmentY = 0;
//
//
//        if (horizontal && bulletSpeed > 0) {
////            System.err.println("Going right");
//        } else if (horizontal && bulletSpeed < 0) {
////            System.err.println("Going left");
//            rotationAngle = Math.PI; // 180 degrees for left
////            adjustmentX = -30; // Move 20 pixels left when facing left
//        } else if (vertical && bulletSpeed > 0) {
////            System.err.println("Going down");
//            rotationAngle = Math.PI / 2; // 90 degrees for down
//            adjustmentY = +6;
//        } else if (vertical && bulletSpeed < 0) {
////            System.err.println("Going up");
//            rotationAngle = -Math.PI / 2; // -90 degrees for up
//            adjustmentY = -6;
//        }
//
//        // Apply rotation transformation and adjustment
////        g2d.rotate(rotationAngle, bullet.x-xLvlOffset + img.getWidth(null) / 2, bullet.y-yLvlOffset + img.getHeight(null) / 2);
////        g2d.drawImage(img, (bullet.x- xLvlOffset + adjustmentX), bullet.y- yLvlOffset+adjustmentY, null);
//
//        // Dispose of the created Graphics2D object
//        g2d.dispose();
    }

    @Override
    public void renderUI(Graphics g) {

    }


//    public Rectangle getBulletHitBox() {
//        return bullet;
//    }

    public boolean isBulletDecayed() {
        return bulletDecayed;
    }

    public void setBulletDecayed(boolean bulletDecayed) {
        this.bulletDecayed = bulletDecayed;
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }
}

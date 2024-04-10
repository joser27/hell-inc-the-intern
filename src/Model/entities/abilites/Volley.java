package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;
import Model.utilz.LoadSave;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Volley extends Ability {
    private ArrayList<Projectile> bullets;
    private boolean lookingRight;
    private boolean lookingUp;
    private boolean vertical;
    private boolean horizontal;
    private int bulletSpeed = 6;
    private int bulletSize = 10;
    private int bulletDistance = 75;
    private int bulletUpTime = 0;
    private boolean bulletDecayed = false;
    int newDir;
    Image img;

    public Volley(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);
        bullets = new ArrayList<Projectile>();
        img = LoadSave.GetSpriteAtlas(LoadSave.ARROW_PROJECTILE).getScaledInstance(bulletSize,bulletSize,Image.SCALE_DEFAULT);

    }

    public void update() {
        updateUI();
        //updateVolley();
        if (abilityUsed) {
            for (Projectile bullet : bullets) {
                bullet.updateProjectile();
            }
        }
    }
    public void shootVolley() {
        if (!abilityUsed) {
            bulletDecayed=false;
            abilityUsed = true;

            newDir = player.getFacingDir();//0 = right, 1 = left, 2 = up, 3 = down

            if (newDir==0) {
                lookingRight=true;
                bullets.add(new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x+30, (int) player.getHitBox().y+30,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y+60,img,40));
            }
            if (newDir==1) {
                lookingRight=false;
                bullets.add(new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x-30, (int) player.getHitBox().y+30,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y+60,img,40));
            }
            if (newDir == 2) {
                lookingUp=true;
                bullets.add(new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x+30, (int) player.getHitBox().y-30,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x+60, (int) player.getHitBox().y,img,40));
            }
            if (newDir==3) {
                lookingUp=false;
                bullets.add(new Projectile(player, (int) player.getHitBox().x, (int) player.getHitBox().y-30,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x+30, (int) player.getHitBox().y,img,40));
                bullets.add(new Projectile(player, (int) player.getHitBox().x+60, (int) player.getHitBox().y-30,img,40));
            }

            if (newDir == 0 || newDir==1) {
                horizontal = true;
                vertical=false;
            }
            if (newDir == 2 || newDir == 3) {
                vertical = true;
                horizontal=false;
            }
}
    }

//    public void updateVolley() {
//        if (abilityUsed) {
//            //int newDir = player.getFacingDir();//0 = right, 1 = left, 2 = up, 3 = down
//            if (horizontal) {
//                for (Projectile bullets : bullets) {
//                    if (lookingRight) {
//                        bullets.hitBox.x += bulletSpeed;
//                    } else {
//                        bullets.hitBox.x -= bulletSpeed;
//                    }
//                }
//            } else if (vertical) {
//                for (Projectile bullets : bullets) {
//                    if (lookingUp) {
//                        bullets.hitBox.y -= bulletSpeed;
//                    } else {
//                        bullets.hitBox.y += bulletSpeed;
//                    }
//                }
//            }
//
//            if (!bulletDecayed) {
//                bulletUpTime++;
//                System.out.println(bulletUpTime);
//                if (bulletUpTime >= bulletDistance) {
//                    bulletDecayed = true;
//                    bulletUpTime = 0;
//                    Iterator<Projectile> iterator = bullets.iterator();
//                    while (iterator.hasNext()) {
//                        Projectile bullets = iterator.next();
//                        iterator.remove();
//                    }
//                }
//            }
//
//
//
//        }
//    }

//    private void updateDir() {
//        if (player.get) {
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

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        if (abilityUsed) {

            for (Projectile bullets : bullets) {
                bullets.render(g,xLvlOffset,yLvlOffset);
            }
        }


    }

    @Override
    public void renderUI(Graphics g) {
        if (abilityUsed) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(50 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
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

    public boolean isBulletDecayed() {
        return bulletDecayed;
    }

    public void setBulletDecayed(boolean bulletDecayed) {
        this.bulletDecayed = bulletDecayed;
    }

    public ArrayList<Projectile> getBullet() {
        return bullets;
    }
}

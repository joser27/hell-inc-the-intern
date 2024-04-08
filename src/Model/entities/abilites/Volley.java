package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Volley extends Ability {
    private ArrayList<Rectangle> bullet;
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

    public Volley(Player player, int scale, int xPos, int yPos, int cd) {
        super(player, scale, xPos, yPos, cd);
        bullet = new ArrayList<>();


    }

    public void update() {
        updateUI();
        updateVolley();
    }
    public void shootVolley() {
        if (!abilityUsed) {
            bulletDecayed=false;
            abilityUsed = true;
            bullet.add(new Rectangle((int) (player.getHitBox().x - 35), (int) (player.getHitBox().y - 35), bulletSize, bulletSize));
            bullet.add(new Rectangle((int) player.getHitBox().x, (int) player.getHitBox().y, bulletSize, bulletSize));
            bullet.add(new Rectangle((int) (player.getHitBox().x + 35), (int) (player.getHitBox().y + 35), bulletSize, bulletSize));
            newDir = player.getFacingDir();//0 = right, 1 = left, 2 = up, 3 = down

            if (newDir==0) {
                lookingRight=true;
            }
            if (newDir==1) {
                lookingRight=false;
            }
            if (newDir == 2) {
                lookingUp=true;
            }
            if (newDir==3) {
                lookingUp=false;
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

    public void updateVolley() {
        if (abilityUsed) {
            //int newDir = player.getFacingDir();//0 = right, 1 = left, 2 = up, 3 = down
            if (horizontal) {
                for (Rectangle bullets : bullet) {
                    if (lookingRight) {
                        bullets.x += bulletSpeed;
                    } else {
                        bullets.x -= bulletSpeed;
                    }
                }
            } else if (vertical) {
                for (Rectangle bullets : bullet) {
                    if (lookingUp) {
                        bullets.y -= bulletSpeed;
                    } else {
                        bullets.y += bulletSpeed;
                    }
                }
            }

            if (!bulletDecayed) {
                bulletUpTime++;
                System.out.println(bulletUpTime);
                if (bulletUpTime >= bulletDistance) {
                    bulletDecayed = true;
                    bulletUpTime = 0;
                    Iterator<Rectangle> iterator = bullet.iterator();
                    while (iterator.hasNext()) {
                        Rectangle bullets = iterator.next();
                        iterator.remove();
                    }
                }
            }



        }
    }

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
            g.setColor(Color.CYAN);
            for (Rectangle bullets : bullet) {
                g.fillRect(bullets.x - xLvlOffset, bullets.y - yLvlOffset, bulletSize, bulletSize);
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

    public ArrayList<Rectangle> getBullet() {
        return bullet;
    }
}

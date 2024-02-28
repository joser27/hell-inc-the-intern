package Model;

import java.awt.*;
import java.util.ArrayList;

public class EnchantedArrow extends Ability{
    private Rectangle bullet;
    private boolean vertical;
    private boolean horizontal;
    private int bulletSpeed = 6;
    private int bulletSize = 60;
    private int bulletDistance = 75;
    private int bulletUpTime = 0;
    private boolean bulletDecayed = false;

    public EnchantedArrow(Player player,int scale, int xPos, int yPos) {
        super(player,scale,xPos,yPos);
        bullet = new Rectangle(xPos,yPos,bulletSize,bulletSize);

    }

    @Override
    public void update() {

        if (horizontal) {
            bullet.x += bulletSpeed;

        } else if (vertical) {
            bullet.y += bulletSpeed;
        }

        bulletUpTime++;
        if (bulletUpTime >= bulletDistance) {
            bulletDecayed = true;
        }
    }

    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.CYAN);
        g.fillRect(bullet.x - xLvlOffset, bullet.y- yLvlOffset, bulletSize,bulletSize);
    }


    public Rectangle getBullet() {
        return bullet;
    }

    public void setBullet(Rectangle bullet) {
        this.bullet = bullet;
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

    public int getBulletSize() {
        return bulletSize;
    }

    public void setBulletSize(int bulletSize) {
        this.bulletSize = bulletSize;
    }

    public int getBulletDistance() {
        return bulletDistance;
    }

    public void setBulletDistance(int bulletDistance) {
        this.bulletDistance = bulletDistance;
    }

    public int getBulletUpTime() {
        return bulletUpTime;
    }

    public void setBulletUpTime(int bulletUpTime) {
        this.bulletUpTime = bulletUpTime;
    }

    public boolean isBulletDecayed() {
        return bulletDecayed;
    }

    public void setBulletDecayed(boolean bulletDecayed) {
        this.bulletDecayed = bulletDecayed;
    }
}

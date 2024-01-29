package Model;

import java.awt.*;

public class Bullet {
    private Rectangle bullet;
    private boolean vertical;
    private boolean horizontal;
    private int bulletSpeed = 4;
    private int bulletSize = 10;
    private int bulletDistance = 60;
    private int bulletUpTime = 0;
    private boolean bulletDecayed = false;
    public Bullet(int xPos, int yPos) {
        bullet = new Rectangle(xPos,yPos,bulletSize,bulletSize);
    }

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
    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(bullet.x, bullet.y, bulletSize, bulletSize);
    }

    public Rectangle getBullet() {
        return bullet;
    }

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

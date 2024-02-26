package Model;

import java.awt.*;
import java.util.ArrayList;

public class Volley {
    private ArrayList<Rectangle> bullet;
    private boolean vertical;
    private boolean horizontal;
    private int bulletSpeed = 6;
    private int bulletSize = 10;
    private int bulletDistance = 75;
    private int bulletUpTime = 0;
    private boolean bulletDecayed = false;

    public Volley(int xPos, int yPos) {
        bullet = new ArrayList<>();
        bullet.add(new Rectangle(xPos-35,yPos-35,bulletSize,bulletSize));
        bullet.add(new Rectangle(xPos,yPos,bulletSize,bulletSize));
        bullet.add(new Rectangle(xPos+35,yPos+35,bulletSize,bulletSize));

    }

    public void update() {

        if (horizontal) {
            for (Rectangle bullets : bullet) {
                bullets.x += bulletSpeed;
            }

        } else if (vertical) {
            for (Rectangle bullets : bullet) {
                bullets.y += bulletSpeed;
            }
        }

        bulletUpTime++;
        if (bulletUpTime >= bulletDistance) {
            bulletDecayed = true;
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.setColor(Color.CYAN);
        for (Rectangle bullets : bullet) {
            g.fillRect(bullets.x - xLvlOffset, bullets.y- yLvlOffset, bulletSize,bulletSize);
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

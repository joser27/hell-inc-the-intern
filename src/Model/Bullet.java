package Model;

import Model.utilz.LoadSave;

import java.awt.*;

public class Bullet {
    private Rectangle bullet;
    private boolean vertical;
    private boolean horizontal;
    private int bulletSpeed = 6;
    private int bulletSize = 8;
    private int bulletDistance = 75;
    private int bulletUpTime = 0;
    private boolean bulletDecayed = false;
    Image img;
    public Bullet(int xPos, int yPos) {
        bullet = new Rectangle(xPos,yPos,bulletSize,bulletSize);
        img = LoadSave.GetSpriteAtlas(LoadSave.ARROW_PROJECTILE).getScaledInstance(20,20,Image.SCALE_DEFAULT);
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
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Set the rotation angle and adjustment based on the bullet's direction
        double rotationAngle = 0.0;
        int adjustmentX = 0;
        int adjustmentY = 0;


        if (horizontal && bulletSpeed > 0) {
//            System.err.println("Going right");
        } else if (horizontal && bulletSpeed < 0) {
//            System.err.println("Going left");
            rotationAngle = Math.PI; // 180 degrees for left
//            adjustmentX = -30; // Move 20 pixels left when facing left
        } else if (vertical && bulletSpeed > 0) {
//            System.err.println("Going down");
            rotationAngle = Math.PI / 2; // 90 degrees for down
            adjustmentY = +6;
        } else if (vertical && bulletSpeed < 0) {
//            System.err.println("Going up");
            rotationAngle = -Math.PI / 2; // -90 degrees for up
            adjustmentY = -6;
        }

        // Apply rotation transformation and adjustment
        g2d.rotate(rotationAngle, bullet.x + img.getWidth(null) / 2, bullet.y + img.getHeight(null) / 2);
        g2d.drawImage(img, (bullet.x + adjustmentX) - xLvlOffset, bullet.y+adjustmentY- yLvlOffset, null);

        // Dispose of the created Graphics2D object
        g2d.dispose();
    }



    public Rectangle getBulletHitBox() {
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

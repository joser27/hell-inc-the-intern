package Model.entities.abilites;

import Controller.GameController;
import Model.entities.Player;
import Model.entities.abilites.Ability;

import java.awt.*;

public class EnchantedArrow extends Ability {
    private Rectangle bullet;
    private boolean vertical;
    private boolean horizontal;
    private int bulletSpeed = 6;
    private int bulletSize = 60;
    private int bulletDistance = 75;
    private int bulletUpTime = 0;
    private boolean bulletDecayed = false;

    public EnchantedArrow(Player player, int scale, int xPos, int yPos, int cd) {
        super(player,scale,xPos,yPos, cd);
        bullet = new Rectangle(xPos,yPos,bulletSize,bulletSize);

    }

    @Override
    public void update() {
        updateUI();
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

    @Override
    public void renderUI(Graphics g, int xLvlOffset, int yLvlOffset) {
        g.drawString(Integer.toString(abilityCoolDownTick),160,800);
        if (abilityUsed) {
            g.setColor(new Color(255, 255, 255, 150));

            g.fillRect(90 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, ticker);
        }
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

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
        bullet = new Projectile(player,(int)player.getHitBox().x,(int)player.getHitBox().y,img,60);
        bullet.setProjectileDirection(player.getFacingDir());
    }

    @Override
    public void update() {
        updateUI();
        if (abilityUsed && bullet != null) {
            bullet.updateProjectile();
            if (bullet.hitSomething) {
                bullet.projectileIsDecayed=true;
            }
            if (bullet.projectileIsDecayed) {
                bullet = null;
            }
        }
    }


    @Override
    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
        Graphics2D g2d = (Graphics2D) g.create();

        if (abilityUsed && bullet!=null) {
            bullet.render(g, xLvlOffset, yLvlOffset);

        }
    }

    @Override
    public void renderUI(Graphics g) {

    }


    public Projectile getBullet() {
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

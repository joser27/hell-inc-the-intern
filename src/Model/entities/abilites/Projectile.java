package Model.entities.abilites;

import Model.entities.Player;

import java.awt.*;

public class Projectile {

    public Rectangle hitBox;
    Player player;


    public boolean vertical;
    public boolean horizontal;
    public int projectileSpeed = 6;
    public int projectileSize = 8;
    public int projectileDistance = 75;
    public int projectileUpTime = 0;
    public boolean projectileDecayed = false;
    public Projectile(Player player) {
        this.player = player;
        hitBox = new Rectangle((int) player.getHitBox().x, (int) player.getHitBox().y,projectileSize,projectileSize);
    }

    public void updateProjectile(int facingDir) {
        switch (facingDir) {
            case 0:
                horizontal=true;
//                enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed());
                projectileSpeed = projectileSpeed;
                break;
            case 1:
                horizontal=true;
//                enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed() * -1);
                projectileSpeed = projectileSpeed * -1;
                break;
            case 2:
                vertical=true;
//                enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed() * -1);
                projectileSpeed = projectileSpeed * -1;
                break;
            case 3:
                vertical=true;
//                enchantedArrow.setBulletSpeed(enchantedArrow.getBulletSpeed());
                projectileSpeed = projectileSpeed;
                break;
        }
        if (horizontal) {
            hitBox.x += projectileSpeed;
        } else if (vertical) {
            hitBox.y += projectileSpeed;
        }

//        projectileUpTime++;
//        if (projectileUpTime >= projectileDistance) {
//            projectileDecayed = true;
//        }
    }

//    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
//
//    }



}

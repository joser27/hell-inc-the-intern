package Model.entities.abilites;

import Model.entities.Player;

import java.awt.Image;
import java.awt.Rectangle;

import static Model.utilz.Constants.PlayerConstants.*;

public class Projectile {

    public Rectangle hitBox;
    Player player;
    Image img;
    public boolean vertical;
    public boolean horizontal;
    public int projectileSpeed = 6;
    public int projectileSize = 20;
    public int projectileDistance = 120;
    public int projectileUpTime = 0;
    public boolean projectileIsDecayed = false;
    int releaseDelay;
    int xPos;
    int yPos;
    //Image rotation
    double rotationAngle = 0.0;
    int adjustmentX = 0;
    int adjustmentY = 0;
    public boolean hitSomething = false;
    boolean hasSetDir = false;
    public Projectile(Player player, int xPos, int yPos, Image img, int releaseDelay) {
        this.player = player;
        this.xPos = xPos;
        this.yPos = yPos;
        this.img = img;
        this.releaseDelay = releaseDelay;
        hitBox = new Rectangle((int) xPos, (int) yPos,projectileSize,projectileSize);
    }
    public void hitsPlayer(Player player) {
        if (hitBox.intersects(player.getHitBox())) {
            if (!hitSomething) {
                player.decrementHealth(20);
//                player.setMovementSpeed(player.getMovementSpeed()/2);
                player.setHasSlowEffect();
                hitSomething=true;
            }
        }
    }
    public void setProjectileDirection(int facingDir) {//0 = right, 1 = left, 2 = up, 3 = down
        switch (facingDir) {
            case 0:
                horizontal=true;
                break;
            case 1:

                horizontal=true;
                projectileSpeed = projectileSpeed * -1;
                rotationAngle = Math.PI; // 180 degrees for left
                break;
            case 2:
                vertical=true;
                projectileSpeed = projectileSpeed * -1;
                rotationAngle = -Math.PI / 2; // -90 degrees for up
                adjustmentY = -6;
                break;
            case 3:
                vertical=true;
                rotationAngle = Math.PI / 2; // 90 degrees for down
                adjustmentY = +6;
                break;
        }
    }
    public void updateProjectile() {//0 = right, 1 = left, 2 = up, 3 = down
        releaseDelay--;


        if (releaseDelay<=0) {
            if (!hasSetDir) {
                setProjectileDirection(player.getFacingDir());
                hasSetDir=true;
            }
            if (horizontal) {
                hitBox.x += projectileSpeed;
            } else if (vertical) {
                hitBox.y += projectileSpeed;
            }


            projectileUpTime++;
            if (projectileUpTime >= projectileDistance) {
                projectileIsDecayed = true;
            }
            switch (player.getFacingDir()) {
                case 0 -> player.playerAction = HUMAN_ATTACK_RIGHT;
                case 1 -> player.playerAction = HUMAN_ATTACK_LEFT;
                case 2 -> player.playerAction = HUMAN_ATTACK_UP;
                case 3 -> player.playerAction = HUMAN_ATTACK_DOWN;
            }
        } else {
            hitBox.x = (int) player.getHitBox().x;
            hitBox.y = (int) player.getHitBox().y;
        }
    }

    public java.awt.Image getImage() { return img; }
    public Rectangle getHitBox() { return hitBox; }
    public double getRotationAngle() { return rotationAngle; }
    public int getAdjustmentX() { return adjustmentX; }
    public int getAdjustmentY() { return adjustmentY; }
    public int getReleaseDelay() { return releaseDelay; }
}

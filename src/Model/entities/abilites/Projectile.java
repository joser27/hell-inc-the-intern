package Model.entities.abilites;

import Model.entities.Player;

import java.awt.*;

import static Model.utilz.Constants.PlayerConstants.*;

public class Projectile {

    public Rectangle hitBox;
    Player player;
    Image img;
    public boolean vertical;
    public boolean horizontal;
    public int projectileSpeed = 6;
    public int projectileSize = 20;
    public int projectileDistance = 75;
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
    public Projectile(Player player, int xPos, int yPos, Image img, int releaseDelay) {
        this.player = player;
        this.xPos = xPos;
        this.yPos = yPos;
        this.img = img;
        this.releaseDelay = releaseDelay;
        hitBox = new Rectangle((int) xPos, (int) yPos,projectileSize,projectileSize);
    }
    public boolean hitsPlayer(Player player) {
        if (hitBox.intersects(player.getHitBox())) {
            System.out.println("HIT PLAYER");
            hitSomething=true;
            return true;
        }
        return false;
    }
    public void setProjectileDirection(int facingDir) {
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


        if (releaseDelay<0) {
            if (horizontal) {
                hitBox.x += projectileSpeed;
            } else if (vertical) {
                hitBox.y += projectileSpeed;
            }


            projectileUpTime++;
            if (projectileUpTime >= projectileDistance) {
                projectileIsDecayed = true;

            }
        } else {
            hitBox.x= (int) player.getHitBox().x;
            hitBox.y= (int) player.getHitBox().y;
        }
    }

    public void render(Graphics g, int xLvlOffset, int yLvlOffset) {
//        g.setColor(Color.RED);
//        g.fillRect((int) hitBox.x-xLvlOffset, (int) hitBox.y-yLvlOffset,projectileSize,projectileSize);

        Graphics2D g2d = (Graphics2D) g.create();

        // Set the rotation angle and adjustment based on the bullet's direction



//        if (horizontal && bulletSpeed > 0) {
////            System.err.println("Going right");
//        } else if (horizontal && bulletSpeed < 0) {
////            System.err.println("Going left");
//            rotationAngle = Math.PI; // 180 degrees for left
////            adjustmentX = -30; // Move 20 pixels left when facing left
//        } else if (vertical && bulletSpeed > 0) {
////            System.err.println("Going down");
//            rotationAngle = Math.PI / 2; // 90 degrees for down
//            adjustmentY = +6;
//        } else if (vertical && bulletSpeed < 0) {
////            System.err.println("Going up");
//            rotationAngle = -Math.PI / 2; // -90 degrees for up
//            adjustmentY = -6;
//        }

        if (releaseDelay<=0) {
            // Apply rotation transformation and adjustment
            g2d.rotate(rotationAngle, hitBox.x - xLvlOffset + img.getWidth(null) / 2, hitBox.y - yLvlOffset + img.getHeight(null) / 2);
            g2d.drawImage(img, (hitBox.x - xLvlOffset + adjustmentX), hitBox.y - yLvlOffset + adjustmentY, null);
        }
//        g.setColor(Color.BLUE);
//        g.fillRect(hitBox.x-xLvlOffset,hitBox.y-yLvlOffset,hitBox.width,hitBox.height);
            // Dispose of the created Graphics2D object
            g2d.dispose();
            switch (player.getFacingDir()) {//0 = right, 1 = left, 2 = up, 3 = down
                case 0:
                    player.playerAction = HUMAN_ATTACK_RIGHT;
                    break;
                case 1:
                    player.playerAction = HUMAN_ATTACK_LEFT;
                    break;
                case 2:
                    player.playerAction = HUMAN_ATTACK_UP;
                    break;
                case 3:
                    player.playerAction = HUMAN_ATTACK_DOWN;
                    break;
            }


    }



}

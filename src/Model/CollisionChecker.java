package Model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class CollisionChecker {

    public CollisionChecker() {

    }
    public void handleCollisionToPlayer(Entity entity, Entity targets, int xSpeed, int ySpeed) {
        Rectangle2D.Float hitBox = entity.getHitBox();
        hitBox.x += xSpeed;
        // Check Horizontal Collision
        if (hitBox.intersects(targets.getHitBox())) {
            hitBox.x -= xSpeed;
            while (!targets.getHitBox().intersects(hitBox)) {
                hitBox.x += Math.signum(xSpeed);
            }
            hitBox.x -= Math.signum(xSpeed);
            xSpeed = 0;
        }

        // Check Vertical Collision
        hitBox.y += ySpeed;


        if (hitBox.intersects(targets.getHitBox())) {
            hitBox.y -= ySpeed;
            while (!targets.getHitBox().intersects(hitBox)) {
                hitBox.y += Math.signum(ySpeed);
            }
            hitBox.y -= Math.signum(ySpeed);
            ySpeed = 0;

        }
        entity.setHitBox(hitBox);
        entity.updateEntityPos(xSpeed, ySpeed);
    }
    public void handleCollision(Entity entity, Entity[] targets, float xSpeed, float ySpeed) {
        Rectangle2D.Float hitBox = entity.getHitBox();
        // Check Horizontal Collision
        hitBox.x += xSpeed;
        for (int i = 0; i < targets.length; i++) {
            if (targets[i] != entity) {
                Entity target = targets[i];
                Rectangle2D.Float targetHitBox = target.getHitBox();
                if (hitBox.intersects(targetHitBox)) {
                    hitBox.x -= xSpeed;
                    while (!targetHitBox.intersects(hitBox)) {
                        hitBox.x += Math.signum(xSpeed);
                    }
                    hitBox.x -= Math.signum(xSpeed);
                    xSpeed = 0;

                    break;  // Exit the loop once a collision is found
                }
            }
        }
        // Check Vertical Collision
        hitBox.y += ySpeed;
        for (int i = 0; i < targets.length; i++) {
            if (targets[i] != entity) {
                Entity target = targets[i];
                Rectangle2D.Float targetHitBox = target.getHitBox();
                if (hitBox.intersects(targetHitBox)) {
                    hitBox.y -= ySpeed;
                    while (!targetHitBox.intersects(hitBox)) {
                        hitBox.y += Math.signum(ySpeed);
                    }
                    hitBox.y -= Math.signum(ySpeed);
                    ySpeed = 0;
                    break;  // Exit the loop once a collision is found
                }
            }
        }
        entity.setHitBox(hitBox);
        entity.updateEntityPos(xSpeed, ySpeed);
    }

}

package Model;

import Model.entities.Entity;
import Model.Wall;

import java.awt.geom.Rectangle2D;

public class CollisionChecker {

    public CollisionChecker() {

    }

    /** Returns true if the hitbox overlaps any solid tile (world[row][col] == 1). */
    private boolean hitBoxOverlapsSolidTile(Rectangle2D.Float hitBox, int[][] world, int tileSize) {
        if (world == null || world.length == 0) return false;
        int rows = world.length;
        int cols = world[0].length;
        int colMin = Math.max(0, (int) (hitBox.x / tileSize));
        int colMax = Math.min(cols - 1, (int) ((hitBox.x + hitBox.width) / tileSize));
        int rowMin = Math.max(0, (int) (hitBox.y / tileSize));
        int rowMax = Math.min(rows - 1, (int) ((hitBox.y + hitBox.height) / tileSize));
        for (int row = rowMin; row <= rowMax; row++) {
            for (int col = colMin; col <= colMax; col++) {
                if (world[row][col] == 1) {
                    float tx = col * tileSize;
                    float ty = row * tileSize;
                    if (hitBox.intersects(tx, ty, tileSize, tileSize))
                        return true;
                }
            }
        }
        return false;
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
    /**
     * Handles collision with other entities (excluding Wall) and with the tile map (solid layer).
     * Level collision comes only from world[][] so it matches the visible Tiled solid layer.
     */
    public void handleCollision(Entity entity, Entity[] targets, int[][] world, int tileSize, float xSpeed, float ySpeed) {
        Rectangle2D.Float hitBox = entity.getHitBox();
        // Horizontal: try move
        hitBox.x += xSpeed;
        boolean hitX = false;
        for (int i = 0; i < targets.length && !hitX; i++) {
            if (targets[i] != entity && !(targets[i] instanceof Wall)) {
                if (hitBox.intersects(targets[i].getHitBox())) {
                    hitX = true;
                }
            }
        }
        if (!hitX && hitBoxOverlapsSolidTile(hitBox, world, tileSize)) hitX = true;
        if (hitX) {
            hitBox.x -= xSpeed;
            float sign = Math.signum(xSpeed);
            if (sign != 0) {
                while (!hitBoxOverlapsSolidTile(hitBox, world, tileSize)) {
                    hitBox.x += sign;
                    boolean entityHit = false;
                    for (int i = 0; i < targets.length && !entityHit; i++) {
                        if (targets[i] != entity && !(targets[i] instanceof Wall) && hitBox.intersects(targets[i].getHitBox()))
                            entityHit = true;
                    }
                    if (entityHit) break;
                }
                hitBox.x -= sign;
            }
            xSpeed = 0;
        }
        // Vertical: try move
        hitBox.y += ySpeed;
        boolean hitY = false;
        for (int i = 0; i < targets.length && !hitY; i++) {
            if (targets[i] != entity && !(targets[i] instanceof Wall)) {
                if (hitBox.intersects(targets[i].getHitBox())) {
                    hitY = true;
                }
            }
        }
        if (!hitY && hitBoxOverlapsSolidTile(hitBox, world, tileSize)) hitY = true;
        if (hitY) {
            hitBox.y -= ySpeed;
            float sign = Math.signum(ySpeed);
            if (sign != 0) {
                while (!hitBoxOverlapsSolidTile(hitBox, world, tileSize)) {
                    hitBox.y += sign;
                    boolean entityHit = false;
                    for (int i = 0; i < targets.length && !entityHit; i++) {
                        if (targets[i] != entity && !(targets[i] instanceof Wall) && hitBox.intersects(targets[i].getHitBox()))
                            entityHit = true;
                    }
                    if (entityHit) break;
                }
                hitBox.y -= sign;
            }
            ySpeed = 0;
        }
        entity.setHitBox(hitBox);
        entity.updateEntityPos(xSpeed, ySpeed);
    }

}

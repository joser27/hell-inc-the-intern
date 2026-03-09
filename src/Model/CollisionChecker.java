package Model;

import Model.entities.Entity;
import Model.Wall;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

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

    /** Returns true if the hitbox overlaps any pixel-precise collision rectangle. */
    private boolean hitBoxOverlapsRect(Rectangle2D.Float hitBox, List<Rectangle> rects) {
        if (rects == null || rects.isEmpty()) return false;
        for (Rectangle r : rects) {
            if (hitBox.intersects(r.x, r.y, r.width, r.height))
                return true;
        }
        return false;
    }

    /** Combined check: solid tiles OR collision rectangles. */
    private boolean hitBoxOverlapsAnySolid(Rectangle2D.Float hitBox, int[][] world, int tileSize, List<Rectangle> rects) {
        return hitBoxOverlapsSolidTile(hitBox, world, tileSize) || hitBoxOverlapsRect(hitBox, rects);
    }

    public void handleCollisionToPlayer(Entity entity, Entity targets, int xSpeed, int ySpeed) {
        Rectangle2D.Float hitBox = entity.getHitBox();
        hitBox.x += xSpeed;
        if (hitBox.intersects(targets.getHitBox())) {
            hitBox.x -= xSpeed;
            while (!targets.getHitBox().intersects(hitBox)) {
                hitBox.x += Math.signum(xSpeed);
            }
            hitBox.x -= Math.signum(xSpeed);
            xSpeed = 0;
        }

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
     * Handles collision with other entities (excluding Wall), tile-based solids (world[][]),
     * and pixel-precise collision rectangles from the object layer.
     */
    public void handleCollision(Entity entity, Entity[] targets, int[][] world, int tileSize, List<Rectangle> collisionRects, float xSpeed, float ySpeed) {
        Rectangle2D.Float hitBox = entity.getHitBox();
        // Horizontal
        hitBox.x += xSpeed;
        boolean hitX = false;
        for (int i = 0; i < targets.length && !hitX; i++) {
            if (targets[i] != entity && !(targets[i] instanceof Wall)) {
                if (hitBox.intersects(targets[i].getHitBox()))
                    hitX = true;
            }
        }
        if (!hitX && hitBoxOverlapsAnySolid(hitBox, world, tileSize, collisionRects)) hitX = true;
        if (hitX) {
            hitBox.x -= xSpeed;
            float sign = Math.signum(xSpeed);
            if (sign != 0) {
                while (!hitBoxOverlapsAnySolid(hitBox, world, tileSize, collisionRects)) {
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
        // Vertical
        hitBox.y += ySpeed;
        boolean hitY = false;
        for (int i = 0; i < targets.length && !hitY; i++) {
            if (targets[i] != entity && !(targets[i] instanceof Wall)) {
                if (hitBox.intersects(targets[i].getHitBox()))
                    hitY = true;
            }
        }
        if (!hitY && hitBoxOverlapsAnySolid(hitBox, world, tileSize, collisionRects)) hitY = true;
        if (hitY) {
            hitBox.y -= ySpeed;
            float sign = Math.signum(ySpeed);
            if (sign != 0) {
                while (!hitBoxOverlapsAnySolid(hitBox, world, tileSize, collisionRects)) {
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

    /** Backward-compatible overload (no collision rects). */
    public void handleCollision(Entity entity, Entity[] targets, int[][] world, int tileSize, float xSpeed, float ySpeed) {
        handleCollision(entity, targets, world, tileSize, null, xSpeed, ySpeed);
    }
}

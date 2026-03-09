package View;

import Controller.GameController;
import Model.Game;
import Model.LevelLoader;
import Model.entities.Player1;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Renders the game world (level, player, walls, abilities). Single-player, zoomed and centered on player.
 * Uses Y-sorting for depth: entities with larger Y (lower on screen) are drawn on top.
 */
public class GameView {

    /** Toggle with F3. When true, draws player hitbox (green) and map solid tiles (red) for collision debug. */
    private static boolean debugHitbox = false;

    public static void toggleDebugHitboxes() {
        debugHitbox = !debugHitbox;
    }
    /** Nudge sprite down (world pixels) so it lines up with hitbox. Increase if sprite still floats. */
    private static final int SPRITE_OFFSET_Y = 160;

    public void render(Graphics g, Game game, int xLvlOffset, int yLvlOffset) {
        render(g, game, xLvlOffset, yLvlOffset, GameController.GAME_WIDTH, GameController.GAME_HEIGHT);
    }

    /** @param displayWidth  actual panel width (use for fullscreen)
     *  @param displayHeight actual panel height (use for fullscreen) */
    public void render(Graphics g, Game game, int xLvlOffset, int yLvlOffset, int displayWidth, int displayHeight) {
        float zoom = GameController.CAMERA_ZOOM;
        LevelLoader levelLoader = game.getLevelLoader();

        g.setColor(new Color(79, 131, 52, 255));
        g.fillRect(0, 0, displayWidth, displayHeight);

        Graphics2D g2d = (Graphics2D) g.create();
        // Java: last-specified first-applied, so (translate then scale) => world (x,y) -> (x*zoom - xLvlOffset, y*zoom - yLvlOffset). Offset from Playing = playerCenter*zoom - screen/2.
        g2d.translate(-xLvlOffset, -yLvlOffset);
        g2d.scale(zoom, zoom);
        // Draw tile layers that go below the player (above_player is drawn after the player)
        final String abovePlayerLayer = "above_player";
        for (String layerName : levelLoader.getDrawLayerNames()) {
            if (abovePlayerLayer.equals(layerName)) continue;
            drawTileLayer(g2d, levelLoader, layerName);
        }

        Player1 p = game.getPlayer1();
        List<Drawable> drawables = new ArrayList<>();
        int playerSortY = (int) (p.getHitBox().y + p.getHitBox().height);
        Rectangle2D.Float h = p.getHitBox();
        int sw = Player1.getSpriteDrawWidth();
        int sh = Player1.getSpriteDrawHeight();
        // Bottom-center of sprite on hitbox; nudge down so art lines up with hitbox (sprite has empty space below feet)
        int sx = (int) (h.x + (h.width - sw) / 2f);
        int sy = (int) (h.y + h.height - sh) + SPRITE_OFFSET_Y;
        drawables.add(new Drawable(playerSortY, () ->
            g2d.drawImage(p.getCurrentSprite(), sx, sy, null)));
        Collections.sort(drawables, Comparator.comparingInt(d -> d.sortY));
        for (Drawable d : drawables) d.draw.run();

        // Draw above_player layer on top of the player (e.g. roof overhangs, tree canopies)
        drawTileLayer(g2d, levelLoader, abovePlayerLayer);

        if (debugHitbox) {
            drawDebugHitboxes(g2d, game);
        }

        g2d.dispose();
    }

    private void drawTileLayer(Graphics2D g2d, LevelLoader levelLoader, String layerName) {
        int[][] gids = levelLoader.getLayerGids(layerName);
        if (gids == null) return;
        for (int i = 0; i < gids.length; i++) {
            for (int j = 0; j < gids[i].length; j++) {
                int gid = gids[i][j];
                if ((gid & 0x1FFFFFFF) == 0) continue;
                int drawX = GameController.TILE_SIZE * j;
                int drawY = GameController.TILE_SIZE * i;
                Image tile = levelLoader.getTileImage(gid);
                if (tile != null)
                    g2d.drawImage(tile, drawX, drawY, null);
            }
        }
    }

    private void drawDebugHitboxes(Graphics2D g2d, Game game) {
        int tileSize = GameController.TILE_SIZE;
        LevelLoader levelLoader = game.getLevelLoader();
        int[][] world = levelLoader != null ? levelLoader.getWorld() : null;

        // Map solid tiles (collision layer) — semi-transparent red
        if (world != null) {
            g2d.setColor(new Color(255, 0, 0, 90));
            for (int row = 0; row < world.length; row++) {
                for (int col = 0; col < world[row].length; col++) {
                    if (world[row][col] == 1) {
                        g2d.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);
                    }
                }
            }
            g2d.setColor(new Color(255, 0, 0, 180));
            g2d.setStroke(new BasicStroke(1f));
            for (int row = 0; row < world.length; row++) {
                for (int col = 0; col < world[row].length; col++) {
                    if (world[row][col] == 1) {
                        g2d.drawRect(col * tileSize, row * tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        // Object-layer collision rects — semi-transparent blue
        if (levelLoader != null) {
            for (java.awt.Rectangle r : levelLoader.getCollisionRects()) {
                g2d.setColor(new Color(0, 100, 255, 90));
                g2d.fillRect(r.x, r.y, r.width, r.height);
                g2d.setColor(new Color(0, 100, 255, 200));
                g2d.setStroke(new BasicStroke(1f));
                g2d.drawRect(r.x, r.y, r.width, r.height);
            }
        }

        // Player hitbox — green outline
        Player1 p = game.getPlayer1();
        if (p != null && p.getHitBox() != null) {
            Rectangle2D.Float h = p.getHitBox();
            g2d.setColor(new Color(0, 255, 0, 200));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRect((int) h.x, (int) h.y, (int) h.width, (int) h.height);
        }
    }

    private static class Drawable {
        final int sortY;
        final Runnable draw;
        Drawable(int sortY, Runnable draw) { this.sortY = sortY; this.draw = draw; }
    }
}

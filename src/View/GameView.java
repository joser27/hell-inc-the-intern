package View;

import Controller.GameController;
import Model.Game;
import Model.LevelLoader;
import Model.Particle;
import Model.entities.Player1;
import Model.utilz.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
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
    /** Firefly sheet: 64x32, 4 cols x 2 rows; frame 16x16. Draw at this size in world (1.5 tiles so they're clearly visible). */
    private static final int FIREFLY_FRAME_W = 16;
    private static final int FIREFLY_FRAME_H = 16;
    private static final int FIREFLY_DRAW_SIZE = 72;
    private static final int FIREFLY_FRAMES = 4;
    /** Fraction of lifetime used for fade-in (0 = no fade-in). E.g. 0.15 = fade in over first 15% of life. */
    private static final float FIREFLY_FADE_IN_FRACTION = 0.0f;
    /** Fraction of lifetime used for fade-out (0 = no fade-out). E.g. 0.2 = fade out over last 20% of life. */
    private static final float FIREFLY_FADE_OUT_FRACTION = 0.0f;
    private BufferedImage fireflySheet;

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
        final int tileSizePx = GameController.TILE_SIZE;
        for (LevelLoader.Decoration dec : levelLoader.getDecorations()) {
            int sortY = dec.y + tileSizePx;
            Image tileBottom = levelLoader.getTileImage(dec.gid);
            if (tileBottom == null) continue;
            final int decX = dec.x, decY = dec.y;
            final Image decTileBottom = tileBottom;
            final Image decTileTop = dec.gidTop != 0 ? levelLoader.getTileImage(dec.gidTop) : null;
            drawables.add(new Drawable(sortY, () -> {
                int wobble = game.getDecorationRustleOffsetX(decX, decY);
                int dx = decX + wobble;
                g2d.drawImage(decTileBottom, dx, decY, null);
                if (decTileTop != null)
                    g2d.drawImage(decTileTop, dx, decY - tileSizePx, null);
            }));
        }
        int playerSortY = (int) (p.getHitBox().y + p.getHitBox().height);
        Rectangle2D.Float h = p.getHitBox();
        int sw = Player1.getSpriteDrawWidth();
        int sh = Player1.getSpriteDrawHeight();
        // Bottom-center of sprite on hitbox; nudge down so art lines up with hitbox (sprite has empty space below feet)
        int sx = (int) (h.x + (h.width - sw) / 2f);
        int sy = (int) (h.y + h.height - sh) + SPRITE_OFFSET_Y;
        drawables.add(new Drawable(playerSortY, () -> {
            g2d.drawImage(p.getCurrentSprite(), sx, sy, null);
            BufferedImage overlay = p.getCurrentOverlaySprite();
            if (overlay != null)
                g2d.drawImage(overlay, sx, sy, null);
        }));
        Collections.sort(drawables, Comparator.comparingInt(d -> d.sortY));
        for (Drawable d : drawables) d.draw.run();

        // Draw above_player layer on top of the player (e.g. roof overhangs, tree canopies)
        drawTileLayer(g2d, levelLoader, abovePlayerLayer);

        // Fireflies drawn in PlayingView above the night overlay so they are not dimmed

        if (debugHitbox) {
            drawDebugHitboxes(g2d, game);
        }

        g2d.dispose();

        // Debug: player coordinates in screen space (world pixels)
        if (debugHitbox) {
            Rectangle2D.Float hitBox = game.getPlayer1().getHitBox();
            int px = (int) hitBox.x;
            int py = (int) hitBox.y;
            int tileSize = GameController.TILE_SIZE;
            int tileX = px / tileSize;
            int tileY = py / tileSize;
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            g.drawString("Player: " + px + ", " + py + " px", 10, 22);
            g.drawString("Tile: " + tileX + ", " + tileY, 10, 38);
        }
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

    /** Draw fireflies in world space. Call with a Graphics2D that already has camera translate and zoom applied (e.g. after drawing night overlay in PlayingView). */
    public void drawFireflyParticles(Graphics2D g2d, Game game) {
        List<Particle> particles = game.getParticles();
        if (particles.isEmpty()) return;
        if (fireflySheet == null) {
            try {
                fireflySheet = LoadSave.GetSpriteAtlas(LoadSave.FIREFLY_SHEET);
            } catch (Exception ignored) {
                return;
            }
        }
        if (fireflySheet == null) return;
        long nowMs = System.currentTimeMillis();
        // Use nearest neighbor so scaled sprites stay crisp (PlayingView draws night with bilinear before us).
        Object prevInterpolation = g2d.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        Composite prev = g2d.getComposite();
        for (Particle p : particles) {
            int frame = (int) ((nowMs + p.aniOffset) / 200) % FIREFLY_FRAMES;
            // Fade in at start of life
            float fadeIn = 1f;
            if (FIREFLY_FADE_IN_FRACTION > 0f && p.maxLife > 0f) {
                float lifeFrac = p.life / p.maxLife;
                if (lifeFrac >= 1f - FIREFLY_FADE_IN_FRACTION)
                    fadeIn = (1f - lifeFrac) / FIREFLY_FADE_IN_FRACTION;
            }
            // Fade out at end of life
            float fadeOut = 1f;
            if (FIREFLY_FADE_OUT_FRACTION > 0f && p.maxLife > 0f) {
                float lifeFrac = p.life / p.maxLife;
                if (lifeFrac <= FIREFLY_FADE_OUT_FRACTION)
                    fadeOut = lifeFrac / FIREFLY_FADE_OUT_FRACTION;
            }
            float alpha = fadeIn * fadeOut;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
            int sx = frame * FIREFLY_FRAME_W;
            int sy = p.row * FIREFLY_FRAME_H;
            g2d.drawImage(fireflySheet,
                (int) p.x, (int) p.y, (int) p.x + FIREFLY_DRAW_SIZE, (int) p.y + FIREFLY_DRAW_SIZE,
                sx, sy, sx + FIREFLY_FRAME_W, sy + FIREFLY_FRAME_H,
                null);
        }
        g2d.setComposite(prev != null ? prev : AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                prevInterpolation != null ? prevInterpolation : RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    private static class Drawable {
        final int sortY;
        final Runnable draw;
        Drawable(int sortY, Runnable draw) { this.sortY = sortY; this.draw = draw; }
    }
}

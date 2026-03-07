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

    public void render(Graphics g, Game game, int xLvlOffset, int yLvlOffset) {
        int screenWidth = GameController.GAME_WIDTH;
        int screenHeight = GameController.GAME_HEIGHT;
        float zoom = GameController.CAMERA_ZOOM;
        LevelLoader levelLoader = game.getLevelLoader();

        g.setColor(new Color(79, 131, 52, 255));
        g.fillRect(0, 0, screenWidth, screenHeight);

        Graphics2D g2d = (Graphics2D) g.create();
        // Java: last-specified first-applied, so (translate then scale) => world (x,y) -> (x*zoom - xLvlOffset, y*zoom - yLvlOffset). Offset from Playing = playerCenter*zoom - screen/2.
        g2d.translate(-xLvlOffset, -yLvlOffset);
        g2d.scale(zoom, zoom);
        // Draw all tiles; no culling so the map stays visible when the camera moves (clip handles off-screen)
        for (String layerName : levelLoader.getDrawLayerNames()) {
            int[][] gids = levelLoader.getLayerGids(layerName);
            if (gids == null) continue;
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

        Player1 p = game.getPlayer1();
        List<Drawable> drawables = new ArrayList<>();
        // Walls/trees not drawn — Tiled solid layer provides visuals; Wall entities still used for collision
        int playerSortY = (int) (p.getHitBox().y + p.getHitBox().height);
        drawables.add(new Drawable(playerSortY, () -> {
            drawHealthBar(g2d, p.getHitBox(), p.getHealth());
            g2d.drawImage(p.getCurrentSprite(), (p.getxPos() - 9 * GameController.SCALE), p.getyPos() - 8 * GameController.SCALE, null);
        }));
        Collections.sort(drawables, Comparator.comparingInt(d -> d.sortY));
        for (Drawable d : drawables) d.draw.run();

        g2d.dispose();

        g.setColor(Color.WHITE);
        g.drawString("HP: " + p.getHealth() + " | Coords: " + p.getxPos() / GameController.TILE_SIZE + "," + p.getyPos() / GameController.TILE_SIZE + " | Boosts: " + p.getSpeedBoostUsages(), 20, 30);
    }

    private static class Drawable {
        final int sortY;
        final Runnable draw;
        Drawable(int sortY, Runnable draw) { this.sortY = sortY; this.draw = draw; }
    }

    private void drawHealthBar(Graphics g, Rectangle2D.Float hitBox, int health) {
        int x = (int) (hitBox.x - 10);
        int y = (int) (hitBox.y - 20);
        g.setColor(new Color(178, 26, 26));
        g.drawRect(x, y, 50, 15);
        g.fillRect(x, y, (int) (health / 2), 15);
        g.setColor(new Color(255, 0, 0));
        g.fillRect(x, y, (int) (health / 2), 10);
    }

}

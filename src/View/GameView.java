package View;

import Controller.GameController;
import Model.Game;
import Model.LevelLoader;
import Model.Wall;
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
        int[][] world = LevelLoader.world;

        g.setColor(new Color(79, 131, 52, 255));
        g.fillRect(0, 0, screenWidth, screenHeight);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.scale(zoom, zoom);
        g2d.translate(-xLvlOffset, -yLvlOffset);
        int visibleW = (int) Math.ceil(screenWidth / zoom) + GameController.TILE_SIZE;
        int visibleH = (int) Math.ceil(screenHeight / zoom) + GameController.TILE_SIZE;

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                int drawX = GameController.TILE_SIZE * j;
                int drawY = GameController.TILE_SIZE * i;
                if (drawX + GameController.TILE_SIZE < xLvlOffset || drawX > xLvlOffset + visibleW) continue;
                if (drawY + GameController.TILE_SIZE < yLvlOffset || drawY > yLvlOffset + visibleH) continue;
                if (world[i][j] == 0)
                    g2d.drawImage(levelLoader.getGrassImage(), drawX, drawY, null);
                if (world[i][j] == 3)
                    g2d.drawImage(levelLoader.getRock1Image(), drawX, drawY, null);
                if (world[i][j] == 4)
                    g2d.drawImage(levelLoader.getRock2Image(), drawX, drawY, null);
            }
        }

        Player1 p = game.getPlayer1();
        List<Drawable> drawables = new ArrayList<>();
        for (Wall w : game.getWalls()) {
            int wx = (int) w.getHitBox().x - w.getDrawOffsetX();
            int wy = (int) w.getHitBox().y - w.getDrawOffsetY();
            if (wx + w.getImage().getWidth(null) < xLvlOffset || wx > xLvlOffset + visibleW) continue;
            if (wy + w.getImage().getHeight(null) < yLvlOffset || wy > yLvlOffset + visibleH) continue;
            int sortY = (int) (w.getHitBox().y + w.getHitBox().height);
            Image wallImg = w.getImage();
            drawables.add(new Drawable(sortY, () -> g2d.drawImage(wallImg, wx, wy, null)));
        }
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

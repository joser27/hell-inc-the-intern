package View;

import Controller.GameController;
import Model.Game;
import Model.LevelLoader;
import Model.Wall;
import Model.entities.Player1;
import Model.entities.Player2;
import Model.entities.abilites.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Renders the game world (level, players, walls, abilities).
 * Reads only from Model; no game logic.
 */
public class GameView {

    public void renderLeftScreen(Graphics g, Game game, int xLvlOffset, int yLvlOffset) {
        int screenWidth = GameController.GAME_WIDTH;
        int screenHeight = GameController.GAME_HEIGHT;
        LevelLoader levelLoader = game.getLevelLoader();
        int[][] world = LevelLoader.world;

        g.setColor(new Color(79, 131, 52, 255));
        g.fillRect(0, 0, screenWidth / 2, screenHeight);

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if ((j * GameController.TILE_SIZE) - xLvlOffset < GameController.GAME_WIDTH / 2) {
                    if (world[i][j] == 0)
                        g.drawImage(levelLoader.getGrassImage(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i - yLvlOffset, null);
                    if (world[i][j] == 3)
                        g.drawImage(levelLoader.getRock1Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i - yLvlOffset, null);
                    if (world[i][j] == 4)
                        g.drawImage(levelLoader.getRock2Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i - yLvlOffset, null);
                }
            }
        }

        drawPlayer(g, game.getPlayer2(), xLvlOffset, yLvlOffset);
        if (game.getPlayer1().getxPos() - xLvlOffset < GameController.GAME_WIDTH / 2)
            drawPlayer(g, game.getPlayer1(), xLvlOffset, yLvlOffset);

        for (Wall w : game.getWalls()) {
            if (w.getHitBox().x - xLvlOffset < GameController.GAME_WIDTH / 2)
                drawWall(g, w, xLvlOffset, yLvlOffset);
        }

        g.setColor(Color.WHITE);
        g.drawString("Player2 coords: " + game.getPlayer2().getxPos() / GameController.TILE_SIZE + " " + game.getPlayer2().getyPos() / GameController.TILE_SIZE + "; HP: " + game.getPlayer2().getHealth(), 80, 150);
    }

    public void renderRightScreen(Graphics g, Game game, int xLvlOffset, int yLvlOffset) {
        int screenWidth = GameController.GAME_WIDTH;
        int screenHeight = GameController.GAME_HEIGHT;
        LevelLoader levelLoader = game.getLevelLoader();
        int[][] world = LevelLoader.world;

        g.setColor(new Color(79, 131, 52, 255));
        g.fillRect(screenWidth / 2, 0, screenWidth / 2, screenHeight);

        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                if ((j * GameController.TILE_SIZE) - xLvlOffset > GameController.GAME_WIDTH / 2 && (j * GameController.TILE_SIZE) - xLvlOffset < GameController.GAME_WIDTH) {
                    if (world[i][j] == 0)
                        g.drawImage(levelLoader.getGrassImage(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i - yLvlOffset, null);
                    if (world[i][j] == 3)
                        g.drawImage(levelLoader.getRock1Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i - yLvlOffset, null);
                    if (world[i][j] == 4)
                        g.drawImage(levelLoader.getRock2Image(), GameController.TILE_SIZE * j - xLvlOffset, GameController.TILE_SIZE * i - yLvlOffset, null);
                }
            }
        }

        drawPlayer(g, game.getPlayer1(), xLvlOffset, yLvlOffset);
        if (game.getPlayer2().getxPos() - xLvlOffset > GameController.GAME_WIDTH / 2 && game.getPlayer2().getxPos() - xLvlOffset < GameController.GAME_WIDTH)
            drawPlayer(g, game.getPlayer2(), xLvlOffset, yLvlOffset);

        for (Wall w : game.getWalls()) {
            if (w.getHitBox().x - xLvlOffset > screenWidth / 2 && w.getHitBox().x - xLvlOffset < screenWidth)
                drawWall(g, w, xLvlOffset, yLvlOffset);
        }

        g.setColor(Color.WHITE);
        g.drawString("Player1 coords: " + game.getPlayer1().getxPos() / GameController.TILE_SIZE + " " + game.getPlayer1().getyPos() / GameController.TILE_SIZE + ", Boosts: " + game.getPlayer1().getSpeedBoostUsages() + "; HP:" + game.getPlayer1().getHealth(), 80, 100);
    }

    private void drawWall(Graphics g, Wall w, int xLvlOffset, int yLvlOffset) {
        g.drawImage(w.getImage(), (int) w.getHitBox().x - w.getDrawOffsetX() - xLvlOffset, (int) w.getHitBox().y - w.getDrawOffsetY() - yLvlOffset, null);
    }

    private void drawPlayer(Graphics g, Object player, int xLvlOffset, int yLvlOffset) {
        if (player instanceof Player1)
            drawPlayer1(g, (Player1) player, xLvlOffset, yLvlOffset);
        else if (player instanceof Player2)
            drawPlayer2(g, (Player2) player, xLvlOffset, yLvlOffset);
    }

    private void drawPlayer1(Graphics g, Player1 p, int xLvlOffset, int yLvlOffset) {
        drawAbilityWorldEffects(g, p.getSmash(), p.getShield(), p.getRoar(), xLvlOffset, yLvlOffset, p);
        drawHealthBar(g, p.getHitBox(), p.getHealth(), xLvlOffset, yLvlOffset);
        g.drawImage(p.getCurrentSprite(), (p.getxPos() - 9 * GameController.SCALE) - xLvlOffset, p.getyPos() - 8 * GameController.SCALE - yLvlOffset, null);
    }

    private void drawPlayer2(Graphics g, Player2 p, int xLvlOffset, int yLvlOffset) {
        drawVolley(g, p.getVolleyShot(), xLvlOffset, yLvlOffset);
        drawRangedAttack(g, p.getRangedAttacks(), xLvlOffset, yLvlOffset);
        drawEnchantedArrow(g, p.getEnchantedArrow(), xLvlOffset, yLvlOffset);
        drawHealthBar(g, p.getHitBox(), p.getHealth(), xLvlOffset, yLvlOffset);
        g.drawImage(p.getCurrentSprite(), (p.getxPos() - 9 * GameController.SCALE) - xLvlOffset, p.getyPos() - 8 * GameController.SCALE - yLvlOffset, null);
    }

    private void drawEnchantedArrow(Graphics g, Model.entities.abilites.EnchantedArrow enchanted, int xLvlOffset, int yLvlOffset) {
        if (enchanted == null) return;
        java.awt.Rectangle bullet = enchanted.getBullet();
        g.setColor(Color.CYAN);
        g.fillRect(bullet.x - xLvlOffset, bullet.y - yLvlOffset, enchanted.getBulletSize(), enchanted.getBulletSize());
    }

    private void drawHealthBar(Graphics g, Rectangle2D.Float hitBox, int health, int xLvlOffset, int yLvlOffset) {
        int x = (int) (hitBox.x - 10) - xLvlOffset;
        int y = (int) (hitBox.y - 20) - yLvlOffset;
        g.setColor(new Color(178, 26, 26));
        g.drawRect(x, y, 50, 15);
        g.fillRect(x, y, (int) (health / 2), 15);
        g.setColor(new Color(255, 0, 0));
        g.fillRect(x, y, (int) (health / 2), 10);
    }

    private void drawAbilityWorldEffects(Graphics g, Smash smash, Shield shield, Roar roar, int xLvlOffset, int yLvlOffset, Player1 p) {
        if (smash != null && smash.isChargingAttack()) {
            g.setColor(new Color(230, 60, 100, 255));
            g.drawString(Integer.toString(smash.getAppliedDamage()), (int) (p.getHitBox().x + p.getHitBox().width) - xLvlOffset, (int) p.getHitBox().y - 30 - yLvlOffset);
            g.setColor(new Color(200, 100, 100, 50));
            Rectangle2D.Float r = smash.getAttackSmashHitBox();
            g.fillRect((int) r.x - xLvlOffset, (int) r.y - yLvlOffset, (int) r.width, (int) r.height);
        }
        if (shield != null && shield.isAbilityUsed()) {
            g.setColor(new Color(180, 36, 36, 92));
            Rectangle2D.Float h = p.getHitBox();
            g.fillOval((int) (h.x - 10) - xLvlOffset, (int) (h.y - 10) - yLvlOffset, (int) h.width * 2, (int) h.height * 2);
        }
        if (roar != null && roar.isAbilityUsed()) {
            Projectile proj = roar.getProjectile();
            if (proj != null) {
                java.awt.Rectangle h = proj.getHitBox();
                g.setColor(Color.red);
                g.fillOval(h.x - xLvlOffset, h.y - yLvlOffset, h.width, h.height);
            }
        }
    }

    private void drawVolley(Graphics g, Volley volley, int xLvlOffset, int yLvlOffset) {
        if (volley == null || !volley.isAbilityUsed()) return;
        for (Projectile b : volley.getBullets())
            drawProjectile(g, b, xLvlOffset, yLvlOffset);
    }

    private void drawRangedAttack(Graphics g, RangedAttack ranged, int xLvlOffset, int yLvlOffset) {
        if (ranged == null) return;
        Projectile b = ranged.getBullet();
        if (b != null)
            drawProjectile(g, b, xLvlOffset, yLvlOffset);
    }

    private void drawProjectile(Graphics g, Projectile b, int xLvlOffset, int yLvlOffset) {
        if (b.getReleaseDelay() > 0 || b.getImage() == null) return;
        Graphics2D g2d = (Graphics2D) g.create();
        int cx = b.getHitBox().x - xLvlOffset + b.getImage().getWidth(null) / 2;
        int cy = b.getHitBox().y - yLvlOffset + b.getImage().getHeight(null) / 2;
        g2d.rotate(b.getRotationAngle(), cx, cy);
        g2d.drawImage(b.getImage(), b.getHitBox().x - xLvlOffset + b.getAdjustmentX(), b.getHitBox().y - yLvlOffset + b.getAdjustmentY(), null);
        g2d.dispose();
    }
}

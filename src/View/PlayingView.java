package View;

import Controller.GameController;
import Model.Game;
import Model.entities.Player1;
import Model.entities.Player2;
import Model.gamestates.Playing;
import Model.gamestates.PlayingUI;

import java.awt.*;

/**
 * Renders the Playing state: divider, game world (left/right), and UI.
 */
public class PlayingView {
    private final GameView gameView = new GameView();

    public void render(Graphics g, Game game, Playing playing) {
        g.setColor(Color.BLACK);
        g.drawLine(GameController.GAME_WIDTH / 2, 0, GameController.GAME_WIDTH / 2, GameController.GAME_HEIGHT);

        gameView.renderLeftScreen(g, game, playing.getP2xLvlOffset(), playing.getP2yLvlOffset());
        gameView.renderRightScreen(g, game, playing.getP1xLvlOffset(), playing.getP1yLvlOffset());

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(10));
        g2d.drawLine(GameController.GAME_WIDTH / 2, 0, GameController.GAME_WIDTH / 2, GameController.GAME_HEIGHT);
        g2d.setStroke(new BasicStroke(1));

        drawPlayingUI(g, playing.getPlayingUI());
        drawPlayer1AbilityUI(g, game.getPlayer1());
        drawPlayer2AbilityUI(g, game.getPlayer2());
    }

    private void drawPlayingUI(Graphics g, PlayingUI ui) {
        if (ui == null) return;
        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(GameController.GAME_WIDTH / 2, (GameController.GAME_HEIGHT / 2) + GameController.GAME_HEIGHT / 4, GameController.GAME_WIDTH / 2, GameController.GAME_HEIGHT / 6);
        g.drawImage(ui.getPlayer1_P(), GameController.GAME_WIDTH / 2 + 10 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer1_Q(), GameController.GAME_WIDTH / 2 + 30 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer1_W(), GameController.GAME_WIDTH / 2 + 50 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer1_E(), GameController.GAME_WIDTH / 2 + 70 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer1_R(), GameController.GAME_WIDTH / 2 + 90 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);

        g.setColor(new Color(255, 255, 255, 100));
        g.fillRect(0, (GameController.GAME_HEIGHT / 2) + GameController.GAME_HEIGHT / 4, GameController.GAME_WIDTH / 2, GameController.GAME_HEIGHT / 6);
        g.drawImage(ui.getPlayer2_P(), 10 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer2_Q(), 30 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer2_W(), 50 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer2_E(), 70 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
        g.drawImage(ui.getPlayer2_R(), 90 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, null);
    }

    private void drawPlayer1AbilityUI(Graphics g, Player1 p) {
        if (p.getShield() != null && p.getShield().isAbilityUsed()) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(GameController.GAME_WIDTH / 2 + 50 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, p.getShield().getTicker());
        }
        if (p.getMeleeAttack() != null) { /* no UI */ }
        if (p.getSmash() != null) {
            g.setColor(Color.BLACK);
            g.drawString(Integer.toString(p.getSmash().getAbilityCoolDownTick()), 160, 900);
            if (p.getSmash().isAbilityUsed()) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(GameController.GAME_WIDTH / 2 + 30 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, p.getSmash().getTicker());
            }
        }
        if (p.getRoar() != null && p.getRoar().isAbilityUsed()) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(GameController.GAME_WIDTH / 2 + 70 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, p.getRoar().getTicker());
        }
    }

    private void drawPlayer2AbilityUI(Graphics g, Player2 p) {
        if (p.getRangerFocus() != null) {
            g.drawString(Integer.toString(p.getRangerFocus().getAbilityCoolDownTick()), 160, 800);
            if (p.getRangerFocus().isAbilityUsed()) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(30 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, p.getRangerFocus().getTicker());
            }
        }
        if (p.getEnchantedArrow() != null) {
            g.drawString(Integer.toString(p.getEnchantedArrow().getAbilityCoolDownTick()), 160, 800);
            if (p.getEnchantedArrow().isAbilityUsed()) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(90 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, p.getEnchantedArrow().getTicker());
            }
        }
        if (p.getVolleyShot() != null && p.getVolleyShot().isAbilityUsed()) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(50 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, p.getVolleyShot().getTicker());
        }
        if (p.getHawkshot() != null && p.getHawkshot().isAbilityUsed()) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(70 * GameController.SCALE, GameController.GAME_HEIGHT - 50 * GameController.SCALE, 64, p.getHawkshot().getTicker());
        }
    }
}

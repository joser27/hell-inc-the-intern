package View;

import Controller.GameController;
import Model.Game;
import Model.entities.Player1;
import Model.gamestates.Playing;
import Model.gamestates.PlayingUI;

import java.awt.*;

/**
 * Renders the Playing state: full-screen game world and Player1 ability UI.
 */
public class PlayingView {
    private final GameView gameView = new GameView();

    public void render(Graphics g, Game game, Playing playing) {
        gameView.render(g, game, playing.getXLvlOffset(), playing.getYLvlOffset());
        drawPlayingUI(g, playing.getPlayingUI());
    }

    private void drawPlayingUI(Graphics g, PlayingUI ui) {
        // if (ui == null) return;
        // int barY = GameController.GAME_HEIGHT - 50 * GameController.SCALE;
        // g.setColor(new Color(255, 255, 255, 100));
        // g.fillRect(0, barY - 10 * GameController.SCALE, GameController.GAME_WIDTH, 60 * GameController.SCALE);
        // int startX = GameController.GAME_WIDTH / 2 - (5 * 20 * GameController.SCALE) / 2;
        // g.drawImage(ui.getPlayer1_P(), startX, barY, null);
        // g.drawImage(ui.getPlayer1_Q(), startX + 20 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_W(), startX + 40 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_E(), startX + 60 * GameController.SCALE, barY, null);
        // g.drawImage(ui.getPlayer1_R(), startX + 80 * GameController.SCALE, barY, null);
    }

}
